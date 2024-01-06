$(document).ready(function () {
  $("#csv").change(function () {
    $("#selectedFile").text(
      $("#csv")
        .val()
        .replace(/C:\\fakepath\\/i, "")
    );
  });

  $("#btnSubmit").click(function (event) {
    //validate input fields
    if ($("#csv").val().length > 1) {
      var filename = $("#csv").val();

      var extension = filename.replace(/^.*\./, "");

      if (extension == filename) {
        extension = "";
      } else {
        extension = extension.toLowerCase();
      }

      if (extension != "csv") {
        alert("Only csv formats are allowed!");
        event.preventDefault();
        return;
      }

      //stop submit the form, we will post it manually.
      event.preventDefault();

      // Get form
      var form = $("#fileUploadForm")[0];

      // Create an FormData object
      var data = new FormData(form);

      $("#btnSubmit").prop("disabled", true);

      $(".loading").removeClass("loading--hide").addClass("loading--show");
      $(".result label").hide();
      $(".resultProgress label").show();

      const intervalId = setInterval(function () {
        $.ajax({
          url: `/bin/servlets/articles/import/progress`,
          type: "GET",
          cache: false,
          success: function (data) {
            $(".resultProgress label").text(`${data.progress}`);
          },
        });
      }, 100);

      $.ajax({
        type: "POST",
        enctype: "multipart/form-data",
        url: "/bin/servlets/file-upload",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        success: function (data) {
          // setTimeout(function () {
          console.log(data);
          const total =
            Number(data.createdArticles) + Number(data.skippedArticles);
          $(".result label").text(
            `Total Articles found: ${total}, Created articles: ${data.createdArticles} & Skipped Articles: ${data.skippedArticles}`
          );
          $(".result label").show();
          $(".loading").removeClass("loading--show").addClass("loading--hide");
          $("#btnSubmit").prop("disabled", false);
          $(".resultProgress label").hide();
          clearInterval(intervalId);
          // }, 5000);
        },
        error: function (e) {
          console.log(e);
          $(".result label").text(e.responseText);
          $(".result label").show();
          $(".loading").removeClass("loading--show").addClass("loading--hide");
          $("#btnSubmit").prop("disabled", false);
          $(".resultProgress label").hide();
        },
      });
    } else {
      alert("Please choose a file to continue...");
      // Cancel the form submission
      event.preventDefault();
      return;
    }
  });
});
