
var myClass = function(test) {
    this.name = test;
};

myClass.prototype.showName = function() {
    return this.name;
};

var Result = {
    pinnedPostsList : new Array(),
    index : 0,

    pin : function (post) {
        $(post).toggleClass("pinned");

        if ($(post).hasClass('pinned')) {
            $(post).attr("src", "/resources/img/icons/pinned.png");
            //save in array
            this.pinnedPostsList.push(post);
            this.index++;/*
            $('html,body').animate({scrollTop: $(post).offset().top},'slow');*/
        } else {
            this.pinnedPostsList.splice( $.inArray(post, this.pinnedPostsList), 1);
            this.index--;
            $(post).attr("src", "/resources/img/icons/pin.png");
        }
    },

    goto : function(post) {
        $("html").animate({scrollTop: $(post).offset().top},'slow', function() {
            $(post).parent().fadeTo(300, 0.3).fadeTo(300, 1);
            $(post).parent().fadeTo(300, 0.3).fadeTo(300, 1);
        });
    },

    previousPin : function () {
        if(this.index > 0) {
            this.index --;
        }
        console.log("prev " + this.index);
        var previousPinnedPost = this.pinnedPostsList[this.index];
        this.goto(previousPinnedPost);
    },

    nextPin : function () {
        if(this.index < this.pinnedPostsList.length - 1) {
            this.index++;
        } else if(this.index > this.pinnedPostsList.length - 1) {
            this.index = this.pinnedPostsList.length - 1;
        }

        console.log("next" + this.index);

        var nextPinnedPost = this.pinnedPostsList[this.index];
        this.goto(nextPinnedPost);
    }

};
