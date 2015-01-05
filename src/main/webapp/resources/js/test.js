/**
 * Created by Bogdan.Stefan on 1/2/2015.
 */

var wrap = $("#wrap");

$(window).scroll(function() {

    var width = $('#SemanticWebSearchImg').height();

    var positionFromTop = $(window).scrollTop();

    if (positionFromTop > width) {

        wrap.addClass("fix-search");

        wrap.removeClass("col-xs-70");

        $('#top-search').removeClass("margin-top-xxs-1");
        $('#togglerDisplay').removeClass("no-display");

        if(!($("#toggled_content").find("#sOptions").length == 1)) {
            $("#sOptions").detach().appendTo("#toggled_content");

            $("#displayAs").removeClass("no-display");
            $("#sOptions").removeClass("row");
            $("hr").removeClass("no-display");

        }
        /*
        var searchHegiht = $('#top-search').height();

        $('.top-header').height(searchHegiht+20);
        */
    }/*else {
        wrap.removeClass("fix-search");
        wrap.addClass("col-xs-70");
    }
*/

});

$('#search').keypress(function(){

    console.log( "Handler for .keypress() called." );
    wrap.addClass("fix-search");

    /*remove the margin top from the search search*/
    $('#top-search').removeClass("margin-top-xxs-1");

    /*remove the class col-xs-70 from wrap*/
    $('#wrap').removeClass("col-xs-70");

    /*remove the no-display from the toggler*/
    $('#togglerDisplay').removeClass("no-display");

    /*set hight for header bar
    var searchHegiht = $('#top-search').height();

    $('.top-header').height(searchHegiht+20);*/

    if(!($("#toggled_content").find("#sOptions").length == 1)) {
        console.log("detache sOption bar");
        $("#sOptions").detach().appendTo("#toggled_content");

        $("#displayAs").removeClass("no-display");
        $("#sOptions").removeClass("row");
        $("hr").removeClass("no-display");
    }
});

$('.toggler').on('click',function(){

    $(this).parent().children().toggle();  //swaps the display:none between the two spans
    $(this).parent().parent().find('#toggled_content').slideToggle('fast');  //swap the display of the main content with slide action
});

$("input:checkbox").click(function(){
    if(this.name == "displayOpt") {
        var group = "input:checkbox[name='" + $(this).attr("name") + "']";
        $(group).attr("checked",false);
        $(this).attr("checked",true);

        var link = $(this).attr('rel');

        $("#mainLayout").attr("href",$(this).attr('rel'));
    }
});