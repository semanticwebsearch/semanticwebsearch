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
            console.log("pin manager trying to pin !");
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
        helper :{
          likechecked : function(_data){
          id_question = $(_data).data("id-question");
          id_respunse = $(_data).data("id-response");
          console.log(id_question + ' '+ id_respunse);
          console.log(_data);
          var grandpa = $(_data).parent().parent();
          //console.log(grandpa);
          var data = $(grandpa).find("input:checked");
          this.changeCheckedPropL(data,"like",id_question,id_respunse,"dislike",0);

              ups = $(_data).data("ups");
              downs = $("#dislike" + id_respunse).data("downs");
              $(_data).siblings(".visualFeedback")[0].innerHTML = ups + 1;
              $("#dislike" + id_respunse).siblings(".visualFeedback")[0].innerHTML = downs;

          },
          dislikechecked : function(_data){
            id_question = $(_data).data("id-question");
            id_respunse = $(_data).data("id-response");
            console.log(id_question + ' '+ id_respunse);
            console.log(_data);
            var grandpa = $(_data).parent().parent();
            //console.log(grandpa);
            var data = $(grandpa).find("input:checked");
            this.changeCheckedPropL(data,"dislike",id_question,id_respunse,"like",0);
              downs = $(_data).data("downs");
              ups = $("#like" + id_respunse).data("ups");

              if(downs/ups >= 0.75) {
                  $(_data).parent().parent().parent().parent().parent().hide()
              }

              $(_data).siblings(".visualFeedback")[0].innerHTML = downs + 1;
              $("#like" + id_respunse).siblings(".visualFeedback")[0].innerHTML = ups;

          },
          /*
              works almost the same as changeCheckedProp but
              it will take a list of items from the caller insted
              of looking for the items itself;
           */
          changeCheckedPropL : function (data,from,id_question,id_respounse,itemName,change){
              console.log(data.length + " "+from+ " "+id_question+" "+id_respounse);
              if(data.length == 0){
                Ajax.feedback_resuls(from,id_question,id_respounse, -1);
              }else{
                  Ajax.feedback_resuls(from,id_question,id_respounse, +1);
              }
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
          },
          mapper: function(id, latitude, longitude){
              alert("Working?!");
              alert(id + " " + latitude + " " + longitude);
          }

        } ,
        layout :{
            masonryOptions : {
                itemSelector: '.answer'
            },
            _grid_ : null,
            masoneryActive : false,
            currentLayout : null,
            initGrid : function() {
                $('main').removeClass('list');
                $('main').addClass('grid');
                // remove the checked prop from the list button;

                this.changeCheckedProp(".layout-option","List",0);
                if(this.masoneryActive) {
                    this._grid_.masonry('destroy');
                    this.masoneryActive = false;
                }
                this.masoneryActive = true;
                this._grid_ = $('main').masonry( this.masonryOptions);
            },
            initList : function() {
                if(this.masoneryActive) {
                    this._grid_.masonry('destroy');
                    this.masoneryActive = false;
                }

                $('main').removeClass('grid');
                $('main').addClass('list');



                // remove the checked prop from the list button;
                this.changeCheckedProp(".layout-option","Grid",0);
            },
            restyleitems : function() {
                console.log();
                if(this.currentLayout == "Grid"){
                    if(this.masoneryActive) {
                        this._grid_.masonry('destroy');
                        this.masoneryActive = false;
                    }
                    this.masoneryActive = true;
                    this._grid_ = $('main').masonry( this.masonryOptions);
                }
            }
            ,
            changeCheckedProp : function(itemClass,itemName,change){
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
            }
        }
        ,
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

           /* working on this !!!! */

          /*
              on start of typing it will move the searchbar to top;
              //execute only once !
          */

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

          // adds the grid layout and will remove list layout if its enabled
        }
    }
}

var Ajax = {

    /* Submits the query form
    *  adds the type of search selected by user
    */
    current_questionid : null,
    current_offset : null,
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
    add_more_question: function() {
        console.log("more questions");
        full_url = "/api/query/topSearches";
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
            layoutManager.eventsManager.layout.restyleitems();
        }).fail(function (e) {
            /* will throw error as the respons is not a valid json fromat!*/
            console.log("Error loading services: ");
            console.log(e);

        });
    },

    demo : function() {
        var _this = this;
        $('.answer').remove();

        var temp_url = "/api/query?q=";
        var query = $("#search-input").val();
        console.log("Query: "+ query);
        var full_url = temp_url + query;
        console.log("Url: " + full_url);
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

            current_offset = use_data.dbpedia.length + use_data.freebase.length;

            if(current_offset == 0){
                _this.add_more_question();
                return;
            }
            if(use_data.dbpedia.length > 0)
                current_questionid = use_data.dbpedia[0].questionId;
            if(use_data.freebase.length > 0)
                current_questionid = use_data.freebase[0].questionId;

            console.log(this.current_questionid + ' ' + this.current_offset);

            Template.initializeLayout(use_data);
            layoutManager.eventsManager.layout.restyleitems();

        }).fail(function(e){
            /* will throw error as the respons is not a valid json fromat!*/
            console.log("Error loading services: ");
            console.log(e);
            _this.add_more_question();
        });

    },

    question_url : function(full_url) {
        var _this = this;
        console.log("Url: " + full_url);
        $('.answer').remove();

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

            current_offset = use_data.dbpedia.length + use_data.freebase.length;
            if(current_offset == 0){
                _this.add_more_question();
                return;
            }
            if(use_data.dbpedia.length > 0)
                current_questionid = use_data.dbpedia[0].questionId;
            if(use_data.freebase.length > 0)
                current_questionid = use_data.freebase[0].questionId;
            console.log(current_questionid + ' ' + current_offset);

            Template.initializeLayout(use_data);
            layoutManager.eventsManager.layout.restyleitems();
        }).fail(function (e) {
            /* will throw error as the respons is not a valid json fromat!*/
            console.log("Error loading services: ");
            console.log(e);
            _this.add_more_question();

        });
    },

    more_question_results: function() {
        console.log(current_questionid + ' '+ current_offset);
        var full_url = "/api/question/"+ current_questionid +"?offset="+ current_offset +"&max=10";
        console.log(full_url);
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

            if(use_data.dbpedia.length + use_data.freebase.length == 0) {
                console.log("No more data!");
                $('.more-data').addClass("no-display");
            }
            else
            {
                current_offset += use_data.dbpedia.length + use_data.freebase.length;
                if(use_data.dbpedia.length > 0)
                    current_questionid = use_data.dbpedia[0].questionId;
                if(use_data.freebase.length > 0)
                    current_questionid = use_data.freebase[0].questionId;

                console.log(this.current_questionid + ' ' + this.current_offset);

                Template.initializeLayout(use_data);
                layoutManager.eventsManager.layout.restyleitems();
            }
        }).fail(function (e) {
            /* will throw error as the respons is not a valid json fromat!*/
            console.log("Error loading services: ");
            console.log(e);

        });
    },
    feedback_resuls : function(status,questionId,answerId, count){
        var full_url = "/api/question/"+questionId+"/"+ answerId+"/"+status+"/"+count;
        console.log(full_url);
        $.ajax({
            type: 'POST',
            url: full_url,
            dataType: "text"
        }).done(function (data) {
            console.log(data);
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
        var DATE_TYPE_PERSON = "Person";
        var DATE_TYPE_WEAPON = "Weapon";
        var DATE_TYPE_CONFLICT = "Conflict";
        var DATE_TYPE_ALBUM = "Album";
        var DATE_TYPE_Location = "Location";
        var DATE_TYPE_Education = "EducationInstitution";
        var DATE_TYPE_Song = "Song";
        var ok = false;

        if(_dataType == DATE_TYPE_PERSON) {
            Template.displayPerson(data);
            ok = true;
        }

        if(_dataType == DATE_TYPE_WEAPON) {
            Template.displayWeapon(data);
            ok = true;
        }

        if(_dataType == DATE_TYPE_CONFLICT){
            Template.displayConflict(data);
            ok = true;
        }

        if(_dataType == DATE_TYPE_ALBUM){
            Template.displayAlbum(data);
            ok = true;
        }
        if(_dataType == DATE_TYPE_Location){
            Template.displayLocation(data);
            ok = true;
        }
        if(_dataType == DATE_TYPE_Education){
            Template.displayEducation(data);
            ok = true;
        }
        if(_dataType == DATE_TYPE_Song){
            Template.displaySong(data);
            ok = true;
        }
        if(!ok){
            Template.displayMoreLinks(data);
        }
    },
    /*Display Person
    * data - data that needs to be proccess and displayed
    **/
    displayMoreLinks: function(data){
        var source = $("#more_question-template").html();
        console.log(source);
        var template = Handlebars.compile(source);

        /* var temp_array  = new Array();
         temp_array.push(data.dbpedia);*/

        //console.log(temp_array);
        var html = template({answer : data});
        console.log(html);
        $(main).append(html);
    },
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
        /*var temp_array_freebase = new Array();
        temp_array_freebase.push(data.freebase)*/;
        html = template({answer : data.freebase});
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
    },

    displayConflict: function(data){
        /* dbpedia */

        var source = $("#conflict-template").html();
        console.log(source);
        var template = Handlebars.compile(source);
        
        var html = template({answer : data.dbpedia});
        console.log(html);
        $(main).append(html);

        /* freebase */
        html = template({answer : data.freebase});
        console.log(html);
        $(main).append(html);

    },

    displayAlbum: function(data){
        /* dbpedia */

        var source = $("#album-template").html();
        console.log(source);
        var template = Handlebars.compile(source);
        
        var html = template({answer : data.dbpedia});
        console.log(html);
        $(main).append(html);

        /* freebase */
        html = template({answer : data.freebase});
        console.log(html);
        $(main).append(html);

    },
    displayLocation: function(data){
        var _this = this;
        var source = $("#location-template").html();
        console.log(source);
        var template = Handlebars.compile(source);
        
        var html = template({answer : data.dbpedia});
        console.log(html);
        $(main).append(template({answer : data.dbpedia})).ready(function(){
            console.log(data.dbpedia);
            $.each(data.dbpedia, function(index, value){
                console.log(value);
                if(value.body.geolocation) {
                    console.log(value.body.geolocation.latitude + " "+ value.body.geolocation.longitude);
                    var currentPosition = new google.maps.LatLng(value.body.geolocation.latitude, value.body.geolocation.longitude);
                    var minimapOptions = {
                        
                        center: currentPosition,
                        zoom: 6,
                        
                    };
                    var map = new google.maps.Map(document.getElementById("minimap"+value.id), minimapOptions);
                    var marker = new google.maps.Marker({
                        position: currentPosition,
                        map: map,
                        title: value.body.name
                    });

                }

            });    
        });

        /* freebase */
        html = template({answer : data.freebase});
        console.log(html);
        $(main).append(template({answer : data.freebase})).ready(function(){
            console.log(data.freebase);
            $.each(data.freebase, function(index, value){
                console.log(value);
                if(value.body.geolocation) {
                    console.log(value.body.geolocation.latitude + " "+ value.body.geolocation.longitude);
                    var currentPosition = new google.maps.LatLng(value.body.geolocation.latitude, value.body.geolocation.longitude);
                    var minimapOptions = {
                        
                        center: currentPosition,
                        zoom: 6,
                        
                    };
                    var map = new google.maps.Map(document.getElementById("minimap"+value.id), minimapOptions);
                    var marker = new google.maps.Marker({
                        position: currentPosition,
                        map: map,
                        title: value.body.name
                    });

                }

            });
            
        });
    },
    displayEducation: function(data){
                var _this = this;
        var source = $("#education-template").html();
        console.log(source);
        var template = Handlebars.compile(source);
        
        var html = template({answer : data.dbpedia});
        console.log(html);
        $(main).append(template({answer : data.dbpedia})).ready(function(){
            console.log(data.dbpedia);
            $.each(data.dbpedia, function(index, value){
                console.log(value);
                if(value.body.geolocation) {
                    console.log(value.body.geolocation.latitude + " "+ value.body.geolocation.longitude);
                    var currentPosition = new google.maps.LatLng(value.body.geolocation.latitude, value.body.geolocation.longitude);
                    var minimapOptions = {
                        
                        center: currentPosition,
                        zoom: 6,
                        
                    };
                    var map = new google.maps.Map(document.getElementById("minimap"+value.id), minimapOptions);
                    var marker = new google.maps.Marker({
                        position: currentPosition,
                        map: map,
                        title: value.body.name
                    });

                }

            });    
        });

        /* freebase */
        html = template({answer : data.freebase});
        console.log(html);
        $(main).append(template({answer : data.freebase})).ready(function(){
            console.log(data.freebase);
            $.each(data.freebase, function(index, value){
                console.log(value);
                if(value.body.geolocation) {
                    console.log(value.body.geolocation.latitude + " "+ value.body.geolocation.longitude);
                    var currentPosition = new google.maps.LatLng(value.body.geolocation.latitude, value.body.geolocation.longitude);
                    var minimapOptions = {
                        
                        center: currentPosition,
                        zoom: 6,
                        
                    };
                    var map = new google.maps.Map(document.getElementById("minimap"+value.id), minimapOptions);
                    var marker = new google.maps.Marker({
                        position: currentPosition,
                        map: map,
                        title: value.body.name
                    });

                }

            });
            
        });

    },
    displaySong: function(data){
                var _this = this;
        var source = $("#song-template").html();
        console.log(source);
        var template = Handlebars.compile(source);
        
        var html = template({answer : data.dbpedia});
        console.log(html);
        $(main).append(template({answer : data.dbpedia})).ready(function(){
            console.log(data.dbpedia);
            $.each(data.dbpedia, function(index, value){
                console.log(value);
                if(value.body.geolocation) {
                    console.log(value.body.geolocation.latitude + " "+ value.body.geolocation.longitude);
                    var currentPosition = new google.maps.LatLng(value.body.geolocation.latitude, value.body.geolocation.longitude);
                    var minimapOptions = {
                        
                        center: currentPosition,
                        zoom: 6,
                        
                    };
                    var map = new google.maps.Map(document.getElementById("minimap"+value.id), minimapOptions);
                    var marker = new google.maps.Marker({
                        position: currentPosition,
                        map: map,
                        title: value.body.name
                    });

                }

            });    
        });

        /* freebase */
        html = template({answer : data.freebase});
        console.log(html);
        $(main).append(template({answer : data.freebase})).ready(function(){
            console.log(data.freebase);
            $.each(data.freebase, function(index, value){
                console.log(value);
                if(value.body.geolocation) {
                    console.log(value.body.geolocation.latitude + " "+ value.body.geolocation.longitude);
                    var currentPosition = new google.maps.LatLng(value.body.geolocation.latitude, value.body.geolocation.longitude);
                    var minimapOptions = {
                        
                        center: currentPosition,
                        zoom: 6,
                        
                    };
                    var map = new google.maps.Map(document.getElementById("minimap"+value.id), minimapOptions);
                    var marker = new google.maps.Marker({
                        position: currentPosition,
                        map: map,
                        title: value.body.name
                    });

                }

            });
            
        });

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
    

    $('#search-input').on('input',function(){

        if(detach){

            detach = false;
            $("#header").removeClass("center");
            $("#header").addClass("top");
            $(".img-logo img").prop("src","./../resources/img/logo-md-white.png");

            //set layout list def.

            layoutManager.eventsManager.layout.changeCheckedProp(".layout-option","List",1);
            layoutManager.eventsManager.layout.initList();
        }

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
            layoutManager.eventsManager.layout.initGrid();
            layoutManager.eventsManager.layout.currentLayout = "Grid";
        }
        //check if the list layout is in need
        if(this.name == "List") {
            // add class grid to layout & remove class list from layout
            layoutManager.eventsManager.layout.initList();
            layoutManager.eventsManager.layout.currentLayout = "List";
        }
        /*
         End Display Layout
         */

    });
});
$(window).scroll(function() {
    if($(window).scrollTop() + $(window).height() == $(document).height()) {
       $('.more-data').removeClass("no-display");
    }
});
