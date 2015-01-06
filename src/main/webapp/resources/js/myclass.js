var Result = {
    pinnedPostsList : new Array(),
    index : 0,

    pin : function (post) {
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
  display : function() {
      var source = $("#answerArticle").html();
      var template = Handlebars.compile(source);
      var context = {answerContent: "Neque, vitae, fugiat, libero corrupti officiis sint facilis tempora quidem repudiandae praesentium odit similique adipisci aut. "
      + "Neque, vitae, fugiat, libero corrupti officiis sint facilis tempora quidem repudiandae praesentium odit similique adipisci aut."
       + "Neque, vitae, fugiat, libero corrupti officiis sint facilis tempora quidem repudiandae praesentium odit similique adipisci aut.",
        pinId : "1", likeId : "1", dislikeId : "1"};
      var html = template(context);
      $('#wrap').append(html);
  }
};
