define([], function() {
    var fileTemplate = {

        simpleTemplate :
            "<div id='inline'>" +
            "<h1>{{title}}</h1>" +
            "<p>Published from js: {{date}}</p>" +
            "</div>",

        listTemplate : "{{#each posts}}" +
            "<div id='inline'>" +
            "<h1>{{title}}</h1>" +
            "<p>Published from js: {{date}}</p>" +
            "</div>" +
            "{{/each}}"

    };

    return fileTemplate;
});
