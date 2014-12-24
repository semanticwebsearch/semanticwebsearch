require.config({
   paths: {
       "jquery" : "lib/jquery-2.1.3.min"
   }
});

require(["jquery", "myclass"], function($, c) {
    var myClass = new c("testingg");
    alert(myClass.showName());
});