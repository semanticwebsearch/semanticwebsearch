var Result = {
    pinnedPostsList : new Array(),
    index : 0,

    pin : function (post) {

        // set the pinAction to be displayed

        $("#pinAction").removeClass("no-display");

        $(post).toggleClass("pinned");
        if ($(post).hasClass('pinned')) {
            $(post).attr("src", "/resources/img/icons/pinned.png");
            this.pinnedPostsList.push(post);
            this.index++;
        } else {
            $(post).attr("src", "/resources/img/icons/pin.png");
            this.pinnedPostsList.splice( $.inArray(post, this.pinnedPostsList), 1);
            this.index--;
        }
    },

    goto : function(post) {
        /*
        * shitty stuff to work both in Chrome(overflow at body - level)
        * and mozilla (overflow at HTML - level)
        * the animation is triggered twice, one time for each element,
        * so we use stop to stop one of them
        */
        var containingArticle = $(post).parent().parent().parent();
        $('body, html').stop(true, true).animate({
            scrollTop: containingArticle.offset().top
                - $("#topHeader").height()}, 'slow', function() {
            containingArticle.fadeTo(350, 0.3).fadeTo(350, 1);
        });
    },

    previousPin : function () {
        if(this.index > 0) {
            this.index --;
        }

        var previousPinnedPost = this.pinnedPostsList[this.index];
        this.goto(previousPinnedPost);
    },

    nextPin : function () {
        if(this.index < this.pinnedPostsList.length - 1) {
            this.index++;
        } else if(this.index > this.pinnedPostsList.length - 1) {
            this.index = this.pinnedPostsList.length - 1;
        }

        var nextPinnedPost = this.pinnedPostsList[this.index];
        this.goto(nextPinnedPost);
    }
};

var Template = {
  display : function(url) {
      var source = $("#answerArticle").html();
      var template = Handlebars.compile(source);

      $.getJSON(url)
          .done(function(data) {
              var data2 = { answer : data };
              var html = template(data2);
              $('#answers').append(html);
          })
          .fail(function() {
              console.log( "error" );
          });
  }
};

var Ajax = {
    submitForm : function() {
        var url = $("#searchForm").prop("action") + "/?q=" + $("#search").val() + "&";
        if($("#text").prop("checked") == true) {
            url += $("#text").serialize();
            url += "&";
        }
        if($("#video").prop("checked") == true) {
            url += $("#video").serialize();
            url += "&";
        }
        if($("#image").prop("checked") == true) {
            url += $("#image").serialize();
            url += "&";
        }
        if($("#map").prop("checked") == true) {
            url += $("#map").serialize();
        }
        Template.display(url);
    }
};

function initializeMap() {
    var map;
    var lat = $('#map-canvas > meta:nth-child(1)').prop("content");
    var lng = $('#map-canvas > meta:nth-child(2)').prop("content");

    var coordinates = new google.maps.LatLng(lat,lng);
    var mapOptions = {
        zoom: 12,
        center: coordinates
    };

    map = new google.maps.Map(document.getElementById('map-canvas'),
        mapOptions);

    var marker = new google.maps.Marker({
        position: coordinates,
        map: map,
        title: 'Faculty of Computer Science, Iasi'
    });
}

Handlebars.registerHelper('ifEqual', function (val1, val2, options) {
    if (val1 === val2) {
        return options.fn(this);
    }
});
