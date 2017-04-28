<script type='text/javascript'>
  function encodeImageFileAsURL() {

    var fileToLoad = document.getElementById("show_img");
	var fileReader = new FileReader();
	fileReader.onload = function(fileLoadedEvent) {
        var srcData = fileLoadedEvent.target.result; // <--- data: base64
		var newImage = document.createElement('img');
        newImage.src = srcData;
        alert("Converted Base64 version is " + newImage.outerHTML);
        console.log("Converted Base64 version is " + newImage.outerHTML);
      }
      fileReader.readAsDataURL(fileToLoad);
  }
</script>