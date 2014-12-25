require.config({
   paths: {
       "jquery" : "lib/jquery-2.1.3.min",
       "handlebars" : "lib/handlebars-v2.0.0"
   }
});

require(["jquery", "myclass", "handlebars"], function($, c, handlebars) {
   /* var myClass = new c("testingg");
    alert(myClass.showName());
*/
    inlineTemplate(handlebars);
    fromJsFile(handlebars);
});

function fromJsFile(handlebars) {
    require(["../templates/hbTmpl"], function(tmpl) {
        var template = handlebars.compile(tmpl.listTemplate);
        var context = {posts : [{title: "My New Post", date: "Today from JS file!"},
                        {title: "My New Post", date: "Today from JS file!"},
                        {title: "My New Post", date: "Today from JS file!"}
                        ]};
        var html = template(context);
        $('body').append(html);
    })
}

function inlineTemplate(handlebars) {
    var source = $("#inline-template").html();
    var template = handlebars.compile(source);
    var context = {title: "My New Post", date: "Today!"};
    var html = template(context);
    $('body').append(html);
}