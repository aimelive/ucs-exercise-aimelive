$(document).ready(function () {
  /****************************************************
   * MAIN
   ****************************************************/

  var search = $(".ucg-search");
  var menuZeroLv = $(".navbar__zero-level");
  $(".ucg_search").css("width", "");

  /* RESOLUTION RESIZE
    ============================= */
  if ($(this).width() < 1025) {
    // $(".search").css("height", window.innerHeight);
  }

  /****************************************************
   * SEARCH OVERLAY
   ****************************************************/

  var ucgSearchLink = menuZeroLv.find(".navbar__link.search");

  ucgSearchLink.click(function () {
    if (!search.hasClass("ucg-search--open")) {
      search.addClass("ucg-search--open");

      setTimeout(function () {
        search.addClass("ucg-search--opacity");
      }, 150);

      setTimeout(function () {
        if ($(window).width() <= 1024) {
          $("html, body").css({ "overflow-y": "hidden", height: "100%" });
        }
        searchResize();
      }, 300);
    }
  });

  var searchClose = search.find(".ucg-search--close");

  searchClose.click(function () {
    if (search.hasClass("ucg-search--open")) {
      search.removeClass("ucg-search--opacity");

      if ($(window).width() <= 1024) {
        $("html, body").css({ "overflow-y": "", height: "" });
      }
      setTimeout(function () {
        search.removeClass("ucg-search--open");
      }, 150);
    }
  });
});

function searchResize() {
  $(".searchSuggestion").css("max-height", ""); // reset
  var topH = $(".searchSuggestion").offset().top;
  var newTop = topH + 50;
  var screenH = $(window).height();
  $(".searchSuggestion").css("max-height", screenH - newTop);
}

$(window).on("resize", function () {
  searchResize();
});
