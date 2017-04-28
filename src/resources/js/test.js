
load('src/resources/raphael-min.js');
load('src/resources/dummy.js');
	
var fun1 = function(name) {
			var dataObject = name;
			var obj = {newick : '((cherry_salmon,chum_salmon),atlantic_salmon)'};
			phylocanvas = new Smits.PhyloCanvas(
				dataObject,
				'svgCanvas', 
				500, 500
			);
			print('Hi there from Javascript, ' + name);
};

