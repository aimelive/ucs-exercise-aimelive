function resetImg() {
  $("[data-bg], [data-bg-mobile]").each(function () {
    $(this).css("background-image", ""); // reset all bg
  });
}
function addBg(w) {
  if (w > 1024) {
    resetImg();
    $("[data-bg]").each(function () {
      var imageUrl = $(this).attr("data-bg");
      if (typeof imageUrl !== typeof undefined && imageUrl !== false) {
        $(this).css("background-image", 'url("' + imageUrl + '")');
      }
    });
  } else if (w <= 1024) {
    resetImg();
    $("[data-bg-mobile]").each(function () {
      var imageUrl = $(this).attr("data-bg-mobile");
      if (typeof imageUrl !== typeof undefined && imageUrl !== false) {
        $(this).css("background-image", 'url("' + imageUrl + '")');
      }
    });
  }
}

function oneSliderFix(swiperDom, istance) {
  var w = $(window).width();
  istance.autoplay.stop();
  var totalSlide = $(
    ".heroBanner .swiper-slide:not(.swiper-slide-duplicate)"
  ).length;

  if (totalSlide == 1) {
    if (w > 1025) {
      swiperDom.find(".heroBanner__arrows").hide();
      istance.params.noSwiping = false;
    } else {
      swiperDom.find("[data-pagination]").hide();
      istance.params.simulateTouch = false;
    }
  } else {
    istance.autoplay.start();
    if (w > 1025) {
      swiperDom.find(".heroBanner__arrows").show();
      istance.params.noSwiping = true;
    } else {
      swiperDom.find("[data-pagination]").show();
      istance.params.simulateTouch = true;
    }
  }
}
function extraHash(container) {
  container.find(".hashTag").show();
  container.find(".more-hashtag").hide();
  container.map(function () {
    var sumHashtag = 0;
    var sumHashtagExtra = 0;
    // 1) Get width of all Hashtag with margin and padding
    $(this)
      .find(".hashTag")
      .map(function () {
        sumHashtag = sumHashtag + $(this).outerWidth();
      });
    // 2) Check if hashtag parent has enough space
    if (sumHashtag < $(this).find(".hashTags").width() - 20) {
      // in this case the extra symbol will not be added
    } else {
      // 3) Calculate how to insert the extra symbol
      var hashtagDiv = $(this).find(".hashTags").width() - 70;
      $(this)
        .find(".hashTag")
        .map(function () {
          sumHashtagExtra = sumHashtagExtra + $(this).outerWidth();
          if (sumHashtagExtra > hashtagDiv) {
            $(this).hide();
          }
        });
      $(this).find(".more-hashtag").css("display", "flex");
    }
  });
}

$(document).ready(function () {
  var windowWidthHero = $(window).width();

  // $(".heroBanner-welcome .hero__mask").each(function () {
  //   if ($(this).attr("data-mask") == "dark") {
  //     $(this)
  //       .find(".mask__logo")
  //       .attr(
  //         "src",
  //         "/etc.clientlibs/ucs-exercise-aimelive/clientlibs/clientlib-site/resources/heroBanner__welcome/img/newLight.png"
  //       )
  //       .addClass("mask__logo--white");
  //   } else {
  //     $(this)
  //       .find(".mask__logo")
  //       .attr(
  //         "src",
  //         "/etc.clientlibs/ucs-exercise-aimelive/clientlibs/clientlib-site/resources/heroBanner__welcome/img/newDark.png"
  //       )
  //       .addClass("mask__logo--dark");
  //   }
  // });

  createHeroSlider();
  arrowSliderStyle();

  //Extra Hashtag Logic
  extraHash($(".heroBanner-welcome .hero__lables"));

  // HERO WITH MODAL START
  $(".heroBanner-welcome .heroBanner__slide--type3 .hero__button").click(
    function () {
      var slideModal = $(this).attr("slideModal");
      $(slideModal).css("display", "flex").hide().fadeIn();
      var modalHeight = $(slideModal).css("height");
      $("main").css({ height: modalHeight, overflow: "hidden" });
      $("footer").hide();
      $("header").hide();
    }
  );

  $(".heroBanner-welcome .modal__close").click(function () {
    $(".heroBanner__modal").css("display", "hidden").fadeOut();
    $("main").css({ height: "auto", overflow: "auto" });
    $("footer").show();
    $("header").show();
  });

  // HERO WITH MODAL END

  // More Hashtag Logic
  $(window).on("resize", function () {
    if ($(window).width() !== windowWidthHero) {
      windowWidthHero = $(window).width();
      createHeroSlider();
      extraHash($(".heroBanner-welcome .hero__info"));
    }
  });
});

/****************************************************
 * FUNCTIONS
 ****************************************************/

/* CREATE SLIDER
  ============================= */

function createHeroSlider() {
  var autoplayDuration = $(".heroBanner__container").attr("autoplay-duration");

  // destroy and initialize again
  if ($(".heroBanner-welcome .swiper-slide-active").length > 0) {
    $(".heroBanner-welcome .heroBanner__container")[0].swiper.destroy();
    addBg($(window).width());
  }

  if ($(".heroBanner-welcome .heroBanner__wrapper").children().length == 1) {
    $(".heroBanner__pagination").hide();
    $(".heroBanner__arrows").hide();
    if ($(".heroBanner-welcome .swiper-slide-active").length > 0) {
      $(".heroBanner-welcome .heroBanner__container")[0].swiper.destroy();
      addBg($(window).width());
    }
  } else {
    if ($(window).width() > 1025) {
      heroBannerSwiper = new Swiper(
        ".heroBanner-welcome .heroBanner__container",
        {
          loop: true,
          slidesPerGroup: 1,
          autoResize: true,
          simulateTouch: false,
          autoplay: {
            delay: autoplayDuration,
          },
          loopFillGroupWithBlank: true,
          slidesPerView: "auto",
          on: {
            transitionEnd: function () {
              var active = $(".heroBanner-welcome .swiper-slide-active").attr(
                "data-slide-number"
              );
              $(".heroBanner-welcome [data-number] .current").text(active);
              arrowSliderStyle();
            },
          },
          // Navigation arrows
          navigation: {
            nextEl: ".heroBanner-welcome [data-next-small]",
            prevEl: ".heroBanner-welcome [data-prev-small]",
          },
        }
      );
    } else {
      heroBannerSwiper = new Swiper(
        ".heroBanner-welcome .heroBanner__container",
        {
          spaceBetween: 10,
          autoplay: {
            delay: autoplayDuration,
          },
          paginationClickable: true,
          autoResize: true,
          pagination: {
            el: ".heroBanner-welcome [data-pagination]",
            clickable: true,
          },
        }
      );
    }

    var active = $(".heroBanner-welcome .swiper-slide-active").attr(
      "data-slide-number"
    );
    var total = $(
      ".heroBanner-welcome .swiper-slide:not(.swiper-slide-duplicate)"
    ).length;
    $(".heroBanner-welcome [data-number] .current").text(active);
    $(".heroBanner-welcome [data-number] .total").text(total);
    oneSliderFix($(".heroBanner-welcome"), heroBannerSwiper);
  }
}

/* SLIDER ARROW COLORS LOGIC
  ============================= */

function arrowSliderStyle() {
  if ($(window).width() > 1025) {
    if ($(".swiper-slide-active .hero__mask").attr("data-mask") == "dark") {
      $(".heroBanner-welcome .icon-down").css("color", "#ffffff");
      $(".heroBanner-welcome .total").css("color", "#ffffff");
      $(".heroBanner-welcome [data-number]").css("color", "#ffffff");
    } else {
      $(".heroBanner-welcome .icon-down").css("color", "#00a2c1");
      $(".heroBanner-welcome .total").css("color", "#262626");
      $(".heroBanner-welcome [data-number]").css("color", "#262626");
    }
  }
}
