$(document).ready(function(){
	$('#search').submit(function(event){
		event.preventDefault();

		$('#search .error').remove();

		var query = $('#search input[type=search]').val();

		$('#search input').attr('disabled', 'disabled');

		if(query != ""){
			$('#canvas').append('<h1>Search for &quot;' + query + '&quot;.</h1>');
			//$.get('url?query='+query,function(data){

				// TODO call visualisation
				refreshCobweb(/*data*/);

				$('#search').animate({
					top: -400
				}).fadeOut();

			//});
		}
		else {
			$('#search input').removeAttr('disabled');
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



// Toggle children on click.
function clickNode(d) {
  // RENDER popup OR GOTO article OR RESEARCH
}

function refreshCobweb(data){
  // DATA
  var data = data || {}, links = [], nodes = data.nodes || [], articles = data.articles || [];

  // Link up the items first
  $.each(nodes, function(i,item){
    var source  = i;

    $.each(item.associations, function(i,association){
      var target = itemExists(nodes, association),
        link = {"source": source, "target": target, "value": 5};

        if(linkExists(links, link) === -1){
          links.push(link);
        }
    });
  });

  // Now the articles
  $.each(articles, function(i,article){
    nodes.push(article);

    var source = itemExists(nodes, article),
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
  var width = 970,
      height = 400;

  var force = d3.layout.force()
      .charge(-500)
      .linkDistance(function(d){ return (d.type==="article" ? 100 : 200); })
      .size([width, height]);

  var svg = d3.select("#canvas").append("svg")
      .attr("width", width)
      .attr("height", height);

  force
    .nodes(nodes)
    .links(links)
    .start();

  // Build links
  var link = svg.selectAll("line.link")
        .data(links)
        .enter().append("line")
        .attr("class", "link")
        .style("stroke-width", function(d) { return Math.sqrt(d.value); });

  // Build nodes
  var node = svg.selectAll("g.node")
        .data(nodes)
        .enter().append("g")
        .attr("class", "node")
        .call(force.drag);

  // Set circle
  node.append("circle")
    .attr("r", function(d) { return (d.type==="article" ? 25 : 60); })
    .style("fill", function(d) { return (d.parent ? "#FF0" : (d.type==="article" ? "#FFF1E0" : (d.type==="person" ? "#9F9" : "#9CF"))); });

  // Set title
  node.append("text")
    .attr("dy", ".3em")
    .style("text-anchor", "middle")
    .text(function(d) { return d.name + " (" + itemExists(nodes,d) + ")"; })
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
}