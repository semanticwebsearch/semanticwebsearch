/**
 * Created by Bogdan.Stefan on 1/2/2015.
 */

var wrap = $("#wrap");
var callTimeout;
$('#search').on('input',function(){
    clearTimeout(callTimeout);
    callTimeout = setTimeout(myNamespace.Ajax.submitForm, 1000);
    detachSearchBar();

});

function detachSearchBar(){
    if(!($("#toggled_content").find("#sOptions").length == 1)) {

       // console.log( "Handler for .keypress() called." );
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

        /*Show to medium logo*/
        $("#logo-md").removeClass("no-display");

        /*Delete the alrge logo*/
        $("#SemanticWebSearchImg").remove();

    //    console.log("detache sOption bar");

        $("#sOptions").detach().appendTo("#toggled_content");

        $("#displayAs").removeClass("no-display");
        $("#sOptions").removeClass("row");
        $("hr").removeClass("no-display");
    }
    lookingFor();
}

//looking for : check what am i looking for and set the list or gread layout for display of answers

 function lookingFor(){
      var searchTypes = $("#lookFor input:checkbox:checked");
      var noOfSelectedSearchTypes = searchTypes.length;

     if(noOfSelectedSearchTypes == 1) {
         setLayout("list");
     } else {
         setLayout("grid");
         if(noOfSelectedSearchTypes == 0) {
             $("#lookFor input:checkbox").prop('checked',true);
         }
     }
  };

function setLayout(name){

    var group = $('#displayAs input:checkbox');
    var displayAs = '#displayAs input:checkbox#'+name;
    if($(displayAs).length > 0) {
      //  console.log($(displayAs));
        $("#mainLayout").prop("href", $(displayAs).attr('rel'));

        group.prop("checked",false);
        $(displayAs).prop("checked",true);
    }else{
       // console.log("[setLayout(name)] there is no id :" + name);
    }
}

$('.toggler').on('click',function(){

    $(this).parent().children().toggle();  //swaps the display:none between the two spans
    $(this).parent().parent().find('#toggled_content').slideToggle('fast');  //swap the display of the main content with slide action
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
            $(answer).fadeIn('slow').css("display","inline-block");
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
    }
});