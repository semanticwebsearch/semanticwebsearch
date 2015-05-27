var layoutManager = {

    pinManager : {
               //list which contains the pinned posts
        pinnedPostsList : new Array(),
        index : 0,

        /*
        *   Adds the selected post to the pinned post list and actualizes the index
        *   Updates the pin image (sets it on pinned or unpinned)
        */
        pin : function (post) {
            $("#pinAction").removeClass("no-display");
            $(post).toggleClass("pinned");

            if ($(post).hasClass('pinned')) {
                $(post).attr("src", "./../resources/img/icons/pinned.png");
                this.pinnedPostsList.push(post);
                this.index++;
            } else {
                $(post).attr("src", "./../resources/img/icons/pin.png");
                this.pinnedPostsList.splice( $.inArray(post, this.pinnedPostsList), 1);
                this.index--;

                console.log(this.pinnedPostsList.length);
                if(this.pinnedPostsList.length==0){
                    $("#pinAction").addClass("no-display");
                }
            }
        },

        /*
        *   Moves the screen to selected pinned post (parameter)
        */
        goto : function(post) {
            /*
             * shitty stuff to work both in Chrome(overflow at body - level)
             * and mozilla (overflow at HTML - level)
             * the animation is triggered twice, one time for each element,
             * so we use stop to stop one of them
             */
            var containingArticle = $(post).parent().parent().parent().parent();
            $('body, html').stop(true, true).animate({
                scrollTop: containingArticle.offset().top
                - $("#header").height()}, 'slow', function() {
                containingArticle.fadeTo(350, 0.3).fadeTo(350, 1);
            });
        },

        /*
        *   called when users wants to navigate to previous pin
        *   Updates the index and then calls GOTO with the corresponding post
        */
        previousPin : function () {
            console.log('previus pin was clicked');
            if(this.index > 0) {
                this.index --;
            } else if(this.index == 0) {
                this.index = this.pinnedPostsList.length - 1;
            }

            var previousPinnedPost = this.pinnedPostsList[this.index];
            this.goto(previousPinnedPost);
        },

        /*
         *   called when users wants to navigate to next pin
         *   Updates the index and then calls GOTO with the corresponding post
         */
        nextPin : function () {
            console.log('next pin was clicked');
            if(this.index < this.pinnedPostsList.length - 1) {
                this.index++;
            } else if(this.index >= this.pinnedPostsList.length - 1) {
                this.index = 0;
            }

            var nextPinnedPost = this.pinnedPostsList[this.index];
            this.goto(nextPinnedPost);
        }
    },
    eventsManager : {
        // events ---------------------------------->>
        bindEvents : function() {
            // events -------------------------->>
            detach = true,
            callTimeout = null,
            /*
                takes care of checkbox checked prop in case they are in pare with other checkbox;
            */
             $(document).on('click','.innerCall',function(){
                    // code here
                 Ajax.question_url(this.href);
                 return false;
                });
            $('.search-type-list input[type=checkbox]').on('click',function(){
                console.log("checkbox clicked;");
            });
            $("input[type=checkbox]").click(function(){
                console.log("was click "+this.id);
                /*
                    Display Layout
                    
                    takes care of the display layout and the checked prop of the list and grid checkboxes
                */

                //check if the grid layout is in need
                if(this.name == "Grid") {
                    // add class grid to layout & remove class list from layout
                   addGridLayout();
                }
                //check if the list layout is in need
                if(this.name == "List") {
                    // add class grid to layout & remove class list from layout
                    addListLayout();
                }
                /*
                    End Display Layout
                */
                /*
                    Like & Dislike

                    takes care of the radiobutton effect
                */
                if(this.name == "like") {
                    var grandpa = $(this).parent().parent();
                    console.log(grandpa);
                    var data = $(grandpa).find("input:checked");
                    changeCheckedPropL(data,"dislike",0);
                }
                if(this.name == "dislike"){
                    var grandpa = $(this).parent().parent();
                    console.log(grandpa);
                    var data = $(grandpa).find("input:checked");
                    changeCheckedPropL(data,"like",0);
                }
                /*
                    End Like & Dislike
                */
            });
            /*
                on start of typing it will move the searchbar to top;
                //execute only once !
            */
            $('#search-input').on('input',function(){

                if(detach){

                    detach = false;

                    $("#header").removeClass("center");
                    $("#header").addClass("top");
                    $(".img-logo img").prop("src","./../resources/img/logo-md-white.png");

                    //see what type of layout should be displayed based on the type of content

                    var checked_inputs = $('.search-type input:checked');
                    var checked_inputs_lenght = checked_inputs.length;
                    if(checked_inputs.length == 1){
                        // add class grid to layout & remove class list from layout
                        addListLayout();
                        changeCheckedProp(".layout-option","List",1);
                    }else{
                        //if none were selected so look for all types of content
                        if(checked_inputs_lenght == 0){
                            //select all the inputs;
                            $('.search-type input').prop('checked',true);
                        }
                        addGridLayout();
                        changeCheckedProp(".layout-option","Grid",1);
                        }
                }

                /*
                * Binds the input event to the query input
                * sets a timeout before querying the server for data
                */
                //clearTimeout(this.callTimeout);
                //console.log("call for data");
                //this.callTimeout = setTimeout(Ajax.submitForm, 1000);
            });
            /*
                takes care of the toggle efect for the search option wich takes to much space
                on small devices
            */
            $('#toggle').on('click',function(){
                
                $(".search-option").toggle('fast');
            });

            $( window ).resize(function(){
                /* set  display:block for the toggle elements when the divice is a medium or bigger*/
                var window_size =$(window).width();
                if( window_size >= 720) {
                    $(".search-option").css('display','block');
                }
            });
             // functions ------------------------------>>
             /*
                changes the checked prop from a checkbox given: 
                parent class of the checkbox
                the name of the checkbox
                bool attr that removes or adds the checked prop
             */
            function changeCheckedProp(itemClass,itemName,change){
                var query;
                if(!change) {
                    query = itemClass+" input:checked";
                }else {
                    query = itemClass+" input";
                }
                var checked_inputs = $( query );
                    console.log(checked_inputs.length);

                    for(i = 0; i < checked_inputs.length; i++) {
                        console.log(checked_inputs[i]);
                        var temp = checked_inputs[i];
                        if(temp.id == itemName) {
                            if(!change){
                                $(temp).prop('checked',false);
                            }else {
                                $(temp).prop('checked',true);
                            }
                            break;
                        }
                    }
            };
             /*
                works almost the same as changeCheckedProp but
                it will take a list of items from the caller insted
                of looking for the items itself;
             */
            function changeCheckedPropL(data,itemName,change){
                console.log(data.length);
                for(i = 0; i < data.length; i++){
                    temp = data[i];
                    console.log(temp);
                    if(temp.name == itemName){
                        if(!change){
                            $(temp).prop('checked',false);
                        }else {
                            $(temp).prop('checked',true);
                        }
                    }
                }
            };
            // adds the grid layout and will remove list layout if its enabled
            function addGridLayout(){
                $('main').removeClass('list');
                $('main').addClass('grid');
                // remove the checked prop from the list button;
                changeCheckedProp(".layout-option","List",0);
                
                // add masonary layout
                var container = $('.grid');
                container.masonry({
                  itemSelector: '.answer'
                });
            };
            // adds the list layout and will remove the grid layout if its enabled
            function addListLayout (){
                $('main').removeClass('grid');
                $('main').addClass('list');
                // remove the style attr from answers and main;
                $('main').removeAttr('style');
                $(".answer").removeAttr('style');
                
                // remove the checked prop from the list button;
                changeCheckedProp(".layout-option","Grid",0);
            };
        }
    }
}

var Ajax = {
        /* Submits the query form
    *  adds the type of search selected by user
    */
    submitForm : function() {
        var url = $("#searchForm").prop("action") + "/?q=" + $("#search-input").val() + "&";
        if($("#Text").prop("checked") == true) {
            url += $("#Text").serialize();
            url += "&";
        }
        if($("#Video").prop("checked") == true) {
            url += $("#Video").serialize();
            url += "&";
        }
        if($("#Image").prop("checked") == true) {
            url += $("#Image").serialize();
            url += "&";
        }
        if($("#Map").prop("checked") == true) {
            url += $("#Map").serialize();
        }
        Template.display(url);
    },

    /*
    * Queries the server and retrieves only one type of info (img, video, text or map)
    */
    dataForType : function(type) {
        var url = $("#searchForm").prop("action") + "/?q=" + $("#search").val() + "&";
        url += type + "=true";
        Template.display(url);
    },

    demo : function() {
        var temp_url = "/api/query?q=";
        var query = $("#search-input").val();
        console.log("Query: "+ query);
        var full_url = temp_url + query;
        console.log("Url: " + full_url)

        $.ajax({
            type : 'GET',
            url: full_url,
            dataType: "text"
        }).done(function(data){
            console.log(" loading services: ");
            console.log(data);

            var use_data =jQuery.parseJSON(data);
            console.log("-----");
            console.log(use_data);

            Template.initializeLayout(use_data);

        }).fail(function(e){
            /* will throw error as the respons is not a valid json fromat!*/
            console.log("Error loading services: ");
            console.log(e);
        });
    },
    question_url : function(full_url) {

    console.log("Url: " + full_url)

        $.ajax({
            type: 'GET',
            url: full_url,
            dataType: "text"
        }).done(function (data) {
            console.log(" loading services: ");
            console.log(data);

            var use_data = jQuery.parseJSON(data);
            console.log("-----");
            console.log(use_data);

            Template.initializeLayout(use_data);

        }).fail(function (e) {
            /* will throw error as the respons is not a valid json fromat!*/
            console.log("Error loading services: ");
            console.log(e);
        });
    }
}
var Template = {
    /*
    *   Queries the server for information, compiles the template and populates it and
    *   after that it appends it to the DOM
    */
    display : function(url) {
        var source = $("#answerArticle").html();
        var template = Handlebars.compile(source);

        $.getJSON(url)
            .done(function(data) {
                var data2 = { answer : data };
                var html = template(data2);
                $(main).append(html);

                layoutChanger();
            })
            .fail(function() {
                console.log( "error" );
            });
    },
    /*Initialize the google map
    * id - the div id in which the map will be placed
    * */
    initializeMap : function(id) {
        var map;
        var lat = $('#map-canvas > meta:nth-child(1)').prop("content");
        var lng = $('#map-canvas > meta:nth-child(2)').prop("content");

        var coordinates = new google.maps.LatLng(lat,lng);
        var mapOptions = {
            zoom: 12,
            center: coordinates
        };

        map = new google.maps.Map(document.getElementById('map-canvas' + id),
            mapOptions);

        var marker = new google.maps.Marker({
            position: coordinates,
            map: map,
            title: 'Faculty of Computer Science, Iasi'
        });
    },

    /*Initialize the layout
    * data - the data from the backend (json format)
    **/
    initializeLayout : function(data) {

        var _dataType = data.entityType;
        console.log(_dataType);
        var DATE_TYPE_PERSON = "Person";
        var DATE_TYPE_WEAPON = "Weapon";

        if(_dataType == DATE_TYPE_PERSON) {
            Template.displayPerson(data);
        }

        if(_dataType == DATE_TYPE_WEAPON) {
            Template.displayWeapon(data);
        }
    },
    /*Display Person
    * data - data that needs to be proccess and displayed
    **/
    displayPerson : function(data) {
        /* dbpedia */
        var source = $("#person-template").html();
        console.log(source);
        var template = Handlebars.compile(source);

       /* var temp_array  = new Array();
        temp_array.push(data.dbpedia);*/

        //console.log(temp_array);
        var html = template({answer : data.dbpedia});
        console.log(html);
        $(main).append(html);

        /* freebase */
        var temp_array_freebase = new Array();
        temp_array_freebase.push(data.freebase);
        html = template({answer : temp_array_freebase});
        $(main).append(html);
    },
    displayWeapon : function(data){
        /* dbpedia only! */

        var source = $("#weapon-template").html();
        console.log(source);
        var template = Handlebars.compile(source);
        
        var html = template({answer : data.dbpedia});
        console.log(html);
        $(main).append(html);
    }

}

$(document).ready(function(){
    console.log('doc ready');

    layoutManager.eventsManager.bindEvents();

    Handlebars.registerHelper('ifEqual', function (v1, v2, options) {
        if(v1 === v2) {
            return options.fn(this);
        }
        return options.inverse(this);
    });
});

