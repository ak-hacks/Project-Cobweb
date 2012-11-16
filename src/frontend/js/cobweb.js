$(document).ready(function(){

  $(window).hashchange(function() {
    var query = location.hash.replace(/^#/, '');

    if(query !== ""){
        doSearch(query, function(){
          $('#search').animate({
            top: -400
          }).fadeOut();
        });
    }
    else {
      $('#search').css('top','').show();
      $('#search input').removeAttr('disabled');
      $('#search .loading').remove();
    }
  });

  $(window).hashchange();

	$('#search').submit(function(event){
		event.preventDefault();

		$('#search .error, #search .loading').remove();

		var query = $('#search input[type=search]').val();

		$('#search input').attr('disabled', 'disabled');
    $('#search input[type=submit]').after('<div class="loading"><img src="images/ajax-loader.gif" alt="Loading ..." /></div>');

		if(query !== ""){
        location.hash = "#" + query;
    }
    else {
      $('#search input').removeAttr('disabled');
      $('#search .loading').remove();
      $('#search input[type=submit]').after('<div class="error">Please enter a search term.</div>');
    }
	});
});

function itemExists(arr, obj){
  var name = (typeof obj === "string" ? obj : obj.name)

  for ( var i = 0, length = arr.length; i < length; i++ ) {
    if ( arr[i].name === name ) {
      return i;
    }
  }

  return -1;
}
function articleExists(arr, obj){
  var url = (typeof obj === "string" ? obj : obj.url)

  for ( var i = 0, length = arr.length; i < length; i++ ) {
    if ( arr[i].url === url ) {
      return i;
    }
  }

  return -1;
}
function linkExists(arr, obj){
  for ( var i = 0, length = arr.length; i < length; i++ ) {
    if ( arr[i].source + "-" + arr[i].target === obj.source + "-" + obj.target ) {
      return i;
    }
    if ( arr[i].source + "-" + arr[i].target === obj.target + "-" + obj.source ) {
      return i;
    }
    if ( arr[i].target + "-" + arr[i].source === obj.source + "-" + obj.target ) {
      return i;
    }
    if ( arr[i].target + "-" + arr[i].source === obj.target + "-" + obj.source ) {
      return i;
    }
  }

  return -1;
}
function articleLinkExists(arr, obj){  
  for ( var i = 0, length = arr.length; i < length; i++ ) {
    if ( arr[i].originalsource + "-" + arr[i].target === obj.originalsource + "-" + obj.target ) {
      return i;
    }
    if ( arr[i].originalsource + "-" + arr[i].target === obj.target + "-" + obj.originalsource ) {
      return i;
    }
    if ( arr[i].target + "-" + arr[i].originalsource === obj.originalsource + "-" + obj.target ) {
      return i;
    }
    if ( arr[i].target + "-" + arr[i].originalsource === obj.target + "-" + obj.originalsource ) {
      return i;
    }
  }

  return -1;
}
function articleLinkCount(arr, obj){ 
  var count = 0; 

  for ( var i = 0, length = arr.length; i < length; i++ ) {
    if ( arr[i].originalsource + "-" + arr[i].target === obj.originalsource + "-" + obj.target ) {
      count++;
    }
    if ( arr[i].originalsource + "-" + arr[i].target === obj.target + "-" + obj.originalsource ) {
      count++;
    }
    if ( arr[i].target + "-" + arr[i].originalsource === obj.originalsource + "-" + obj.target ) {
      count++;
    }
    if ( arr[i].target + "-" + arr[i].originalsource === obj.target + "-" + obj.originalsource ) {
      count++;
    }
  }

  return count;
}
Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length);
  this.length = from < 0 ? this.length + from : from;
  return this.push.apply(this, rest);
};
function clickNode(d) {
  // RENDER popup OR GOTO article OR RESEARCH
  if(d.type === "article"){
    document.location = d.url;
  }
  else {
    location.hash = "#" + d.name;
  }
}

var links = [], nodes = [], articles = [], parent_node = null, width = 970, height = 540, force, svg,
  highlighted_gradient, company_gradient, article_gradient, person_gradient, link,
  node;

function doSearch(query,callback){
  if(typeof callback === "undefined")
    callback = function(){};

  $('#canvas').html('');

  $('#canvas').append('<h1>Search for &quot;' + query + '&quot;.</h1>');
  
  query = query.toLowerCase().replace(/^.|\s\S/g, function(a) { return a.toUpperCase(); });

  $.get('/cobweb/rest/relations/' + query + '.json', function(data){
    callback();
    
    // DATA
    links = [];
    nodes = data.results[0] || [];
    articles = data.results[1] || [];
    parent_node = nodes[0];

    parent_node.parent = true;
    nodes[0] = parent_node;

    // Link up the items first
    $.each(nodes, function(i,item){
      var source  = i;

      $.each(item.associations, function(i,association){
        var target = itemExists(nodes, association),
          link = {"source": source, "target": target, "value": 5};

          if(linkExists(links, link) === -1) {
            links.push(link);
          }
      });
    });

    // Now the articles
    $.each(articles, function(i,article){
      nodes.push(article);

      var source = articleExists(nodes, article),
        link1 = {"source": source, "target": itemExists(nodes, article.belongs[0]), "value": 5, "type": "article", "originalsource": itemExists(nodes, article.belongs[1])},
        link2 = {"source": source, "target": itemExists(nodes, article.belongs[1]), "value": 5, "type": "article", "originalsource": itemExists(nodes, article.belongs[0])},
        existing_item_link_index = linkExists(links, {"source": itemExists(nodes, article.belongs[0]), "target": itemExists(nodes, article.belongs[1]) }),
        num_existing_article_links = articleLinkCount(links, link2),
        existing_article_link_index = articleLinkExists(links, link2),
        existing_article_link;

        // Remove link between items
        if(existing_item_link_index !== -1){
          links.remove(existing_item_link_index);
        }

        // Insert article between link and existing article
        if(existing_article_link_index !== -1){
          existing_article_link = links[existing_article_link_index];
          link2.target = existing_article_link.target;
          existing_article_link.target = source;
          links[existing_article_link_index] = existing_article_link;
        }
        
        if(num_existing_article_links < 2) {
          links.push(link1);
        }

        links.push(link2);
    });

    // RENDER
    force = d3.layout.force()
        .charge(-500)
        .gravity(0.07)
        .linkDistance(function(d){ return (d.type==="article" ? 100 : 200); })
        .size([width, height]);

    svg = d3.select("#canvas").append("svg")
        .attr("width", width)
        .attr("height", height)
        .attr("pointer-events", "all")
        .append('svg:g')
        .call(d3.behavior.zoom().on("zoom", function() {
          svg.attr("transform",
              "translate(" + d3.event.translate + ")"
              + " scale(" + d3.event.scale + ")");
        }))
        .append('svg:g');

        // Extra rect for zooming
        svg.append('svg:rect')
    .attr('width', width * 2)
    .attr('height', height * 2)
    .attr('opacity', 0);

    highlighted_gradient = svg.append("svg:defs")
      .append("svg:linearGradient")
        .attr("id", "highlighted_gradient")
        .attr("x1", "0%")
        .attr("y1", "0%")
        .attr("x2", "0%")
        .attr("y2", "100%")
        .attr("spreadMethod", "pad");
    highlighted_gradient.append("svg:stop")
        .attr("offset", "0%")
        .attr("stop-color", "#FF0")
        .attr("stop-opacity", 1);
    highlighted_gradient.append("svg:stop")
        .attr("offset", "100%")
        .attr("stop-color", "#990")
        .attr("stop-opacity", 1);

    company_gradient = svg.append("svg:defs")
      .append("svg:linearGradient")
        .attr("id", "company_gradient")
        .attr("x1", "0%")
        .attr("y1", "0%")
        .attr("x2", "0%")
        .attr("y2", "100%")
        .attr("spreadMethod", "pad");
    company_gradient.append("svg:stop")
        .attr("offset", "0%")
        .attr("stop-color", "#9CF")
        .attr("stop-opacity", 1);
    company_gradient.append("svg:stop")
        .attr("offset", "100%")
        .attr("stop-color", "#06F")
        .attr("stop-opacity", 1);

    article_gradient = svg.append("svg:defs")
      .append("svg:linearGradient")
        .attr("id", "article_gradient")
        .attr("x1", "0%")
        .attr("y1", "0%")
        .attr("x2", "0%")
        .attr("y2", "100%")
        .attr("spreadMethod", "pad");
    article_gradient.append("svg:stop")
        .attr("offset", "0%")
        .attr("stop-color", "#FFF1E0")
        .attr("stop-opacity", 1);
    article_gradient.append("svg:stop")
        .attr("offset", "100%")
        .attr("stop-color", "#E0D5C5")
        .attr("stop-opacity", 1);

    person_gradient = svg.append("svg:defs")
      .append("svg:linearGradient")
        .attr("id", "person_gradient")
        .attr("x1", "0%")
        .attr("y1", "0%")
        .attr("x2", "0%")
        .attr("y2", "100%")
        .attr("spreadMethod", "pad");
    person_gradient.append("svg:stop")
        .attr("offset", "0%")
        .attr("stop-color", "#9F9")
        .attr("stop-opacity", 1);
    person_gradient.append("svg:stop")
        .attr("offset", "100%")
        .attr("stop-color", "#090")
        .attr("stop-opacity", 1);

    // Hack to filter out my crapy linking algorithm
    links = $.grep(links, function(link){
      if(link.target && link.target !== -1) {
        return true;
      }
    });

    console.log('LINKS',links);

    force
      .nodes(nodes)
      .links(links)
      .start();

    // Build links
    link = svg.selectAll("line.link")
          .data(links)
          .enter().append("line")
          .attr("class", "link")
          .style("stroke-width", function(d) { return Math.sqrt(d.value); });

    // Build nodes
    node = svg.selectAll("g.node")
          .data(nodes)
          .enter().append("g")
          .attr("class", "node")
          .call(force.drag);

    // Set circle
    node.append("circle")
      .attr("r", function(d) { return (d.type==="article" ? 35 : (d.type==="person" ? 50 : 60)); })
      .style("fill", function(d) {
        return "url(#" + (d.parent ? "highlighted_gradient" : (d.type==="article" ? "article_gradient" : (d.type==="person" ? "person_gradient" : "company_gradient"))) + ")";
         });

    // Set title
    node.append("text")
      .attr("dy", ".3em")
      .style("text-anchor", "middle")
      .text(function(d) { return (d.type == "article" ? d.title : d.name) /*+ ' ('+d.index+')'*/; })
      .on("click", clickNode);

    // Layout on page
    force.on("tick", function() {
      link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

      node.attr("cx", function(d) { return d.x; })
          .attr("cy", function(d) { return d.y; })
          .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
    });
  });
}