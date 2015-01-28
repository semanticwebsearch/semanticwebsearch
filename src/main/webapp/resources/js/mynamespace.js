var myNamespace = {

    Result : {
        pinnedPostsList : new Array(),
        index : 0,

        pin : function (post) {
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

                console.log(this.pinnedPostsList.length);
                if(this.pinnedPostsList.length==0){
                    $("#pinAction").addClass("no-display");
                }
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
            } else if(this.index == 0) {
                this.index = this.pinnedPostsList.length - 1;
            }

            var previousPinnedPost = this.pinnedPostsList[this.index];
            this.goto(previousPinnedPost);
        },

        nextPin : function () {
            if(this.index < this.pinnedPostsList.length - 1) {
                this.index++;
            } else if(this.index >= this.pinnedPostsList.length - 1) {
                this.index = 0;
            }

            var nextPinnedPost = this.pinnedPostsList[this.index];
            this.goto(nextPinnedPost);
        }
    },

    Template : {
        display : function(url) {
            var source = $("#answerArticle").html();
            var template = Handlebars.compile(source);

            $.getJSON(url)
                .done(function(data) {
                    var data2 = { answer : data };
                    var html = template(data2);
                    $('#answers').append(html);

                    layoutChanger();
                })
                .fail(function() {
                    console.log( "error" );
                });
        },

        initializeMap : function() {
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
    },

    Ajax : {
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
            myNamespace.Template.display(url);
        },

        dataForType : function(type) {
            var url = $("#searchForm").prop("action") + "/?q=" + $("#search").val() + "&";
            url += type + "=true";
            myNamespace.Template.display(url);
        }
    },

    Helper : {
        callTimeout : null,

        bindEvents : function() {
            $('.toggler').on('click',function(){
                $(this).parent().children().toggle();  //swaps the display:none between the two spans
                $(this).parent().parent().find('#toggled_content').slideToggle('fast');  //swap the display of the main content with slide action
            });

            $('#search').on('input',function(){
                clearTimeout(this.callTimeout);
                console.log("dada");
                this.callTimeout = setTimeout(myNamespace.Ajax.submitForm, 1000);
                detachSearchBar();
            });

            $('#lookFor div label').on('click',function(){

                var name = $(this).parent().find('input').prop('name');
                var answer = ".answer.answer-"+ name;

                if($(this).parent().find('input').get(0).checked){
                    $(answer).fadeOut('fast');
                }else{
                    /*If this type of data does not exists on page,
                     * we will call the server and get that data
                     */
                    console.log($("#answers").find(answer).length);
                    if($("#answers").find(answer).length > 0) {
                        $(answer).fadeIn('slow');
                        layoutChanger();

                    } else {
                        if($.trim($("#search").val()).length > 0) {
                            myNamespace.Ajax.dataForType(name);
                        }
                    }
                }

            });

            //swap the css format for answers display
            $("input:checkbox").click(function(){

                if(this.name == "displayOpt") {

                    var group = "input:checkbox[name='" + $(this).prop("name") + "']";

                    $(group).prop("checked",false);
                    $(this).prop("checked",true);

                    $("#mainLayout").prop("href",$(this).attr('rel'));

                    layoutChanger();
                }
            });
        },

        initialize : function() {
            Handlebars.registerHelper('ifEqual', function (val1, val2, options) {
                if (val1 === val2) {
                    return options.fn(this);
                }
            });
            myNamespace.Helper.bindEvents();
        }

    }
};




