$(function () {
  $("#show_button").click(function () {
    html2canvas(document.body, {
      onrendered: function (canvas) {
        $("<img/>", {
          id: "image",
          src: canvas.toDataURL("image/png"),
          width: '95%',
          height: '95%'
        }).appendTo($("#show_img").empty());
      }
    });
  });
});
downloadPNG = function () {
  html2canvas(document.body, {
    onrendered: function (canvas) {
      Canvas2Image.saveAsPNG(canvas);
    }
  });
}