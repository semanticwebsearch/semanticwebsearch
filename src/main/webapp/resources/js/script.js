var layoutManager={pinManager:{pinnedPostsList:new Array,index:0,pin:function(a){$("#pinAction").removeClass("no-display"),$(a).toggleClass("pinned"),$(a).hasClass("pinned")?($(a).attr("src","./../resources/img/icons/pinned.png"),this.pinnedPostsList.push(a),this.index++):($(a).attr("src","./../resources/img/icons/pin.png"),this.pinnedPostsList.splice($.inArray(a,this.pinnedPostsList),1),this.index--,console.log(this.pinnedPostsList.length),0==this.pinnedPostsList.length&&$("#pinAction").addClass("no-display"))},"goto":function(a){var b=$(a).parent().parent().parent().parent();$("body, html").stop(!0,!0).animate({scrollTop:b.offset().top-$("#header").height()},"slow",function(){b.fadeTo(350,.3).fadeTo(350,1)})},previousPin:function(){console.log("previus pin was clicked"),this.index>0?this.index--:0==this.index&&(this.index=this.pinnedPostsList.length-1);var a=this.pinnedPostsList[this.index];this["goto"](a)},nextPin:function(){console.log("next pin was clicked"),this.index<this.pinnedPostsList.length-1?this.index++:this.index>=this.pinnedPostsList.length-1&&(this.index=0);var a=this.pinnedPostsList[this.index];this["goto"](a)}},eventsManager:{bindEvents:function(){function a(a,b,c){var d;d=c?a+" input":a+" input:checked";var e=$(d);for(console.log(e.length),i=0;i<e.length;i++){console.log(e[i]);var f=e[i];if(f.id==b){c?$(f).prop("checked",!0):$(f).prop("checked",!1);break}}}function b(a,b,c){for(console.log(a.length),i=0;i<a.length;i++)temp=a[i],console.log(temp),temp.name==b&&(c?$(temp).prop("checked",!0):$(temp).prop("checked",!1))}function c(){$("main").removeClass("list"),$("main").addClass("grid"),a(".layout-option","List",0);var b=$(".grid");b.masonry({itemSelector:".answer"})}function d(){$("main").removeClass("grid"),$("main").addClass("list"),$("main").removeAttr("style"),$(".answer").removeAttr("style"),a(".layout-option","Grid",0)}detach=!0,callTimeout=null,$(".search-type-list input[type=checkbox]").on("click",function(){console.log("checkbox clicked;")}),$("input[type=checkbox]").click(function(){if(console.log("was click "+this.id),"Grid"==this.name&&c(),"List"==this.name&&d(),"like"==this.name){var a=$(this).parent().parent();console.log(a);var e=$(a).find("input:checked");b(e,"dislike",0)}if("dislike"==this.name){var a=$(this).parent().parent();console.log(a);var e=$(a).find("input:checked");b(e,"like",0)}}),$("#search-input").on("input",function(){if(detach){detach=!1,$("#header").removeClass("center"),$("#header").addClass("top"),$(".img-logo img").prop("src","./../resources/img/logo-md-white.png");var b=$(".search-type input:checked"),e=b.length;1==b.length?(d(),a(".layout-option","List",1)):(0==e&&$(".search-type input").prop("checked",!0),c(),a(".layout-option","Grid",1))}clearTimeout(this.callTimeout),console.log("call for data"),this.callTimeout=setTimeout(Ajax.submitForm,1e3)}),$("#toggle").on("click",function(){$(".search-option").toggle("fast")}),$(window).resize(function(){var a=$(window).width();a>=720&&$(".search-option").css("display","block")})}}},Ajax={submitForm:function(){var a=$("#searchForm").prop("action")+"/?q="+$("#search-input").val()+"&";1==$("#Text").prop("checked")&&(a+=$("#Text").serialize(),a+="&"),1==$("#Video").prop("checked")&&(a+=$("#Video").serialize(),a+="&"),1==$("#Image").prop("checked")&&(a+=$("#Image").serialize(),a+="&"),1==$("#Map").prop("checked")&&(a+=$("#Map").serialize()),Template.display(a)},dataForType:function(a){var b=$("#searchForm").prop("action")+"/?q="+$("#search").val()+"&";b+=a+"=true",Template.display(b)},demo:function(){var a="/api/query?q=",b=$("#search-input").val();console.log("Query: "+b);var c=a+b;console.log("Url: "+c),$.ajax({type:"GET",url:c,dataType:"text"}).done(function(a){console.log(" loading services: "),console.log(a);var b=jQuery.parseJSON(a);console.log("-----"),console.log(b)}).fail(function(a){console.log("Error loading services: "),console.log(a)})}},Template={display:function(a){var b=$("#answerArticle").html(),c=Handlebars.compile(b);$.getJSON(a).done(function(a){var b={answer:a},d=c(b);$(main).append(d),layoutChanger()}).fail(function(){console.log("error")})},initializeMap:function(a){var b,c=$("#map-canvas > meta:nth-child(1)").prop("content"),d=$("#map-canvas > meta:nth-child(2)").prop("content"),e=new google.maps.LatLng(c,d),f={zoom:12,center:e};b=new google.maps.Map(document.getElementById("map-canvas"+a),f);new google.maps.Marker({position:e,map:b,title:"Faculty of Computer Science, Iasi"})}};$(document).ready(function(){console.log("doc ready"),layoutManager.eventsManager.bindEvents(),Handlebars.registerHelper("ifEqual",function(a,b,c){return a===b?c.fn(this):void 0})});