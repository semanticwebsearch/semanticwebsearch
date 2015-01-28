/**
 * Created by Bogdan.Stefan on 1/2/2015.
 */

var wrap = $("#wrap");


function detachSearchBar(){

    if(($("#search").val().length == 1)) {

        console.log("DADAWDAWDAWDAWDAWDAWDA");
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

        layoutChanger();
    }else{
        console.log("[setLayout(name)] there is no id :" + name);
    }
}
function layoutChanger(){

    var layoutDisplay = $("#displayAs input:checked");
    var displayType;

    console.log(layoutDisplay[0].id);
    if(layoutDisplay[0].id=="grid"){
        displayType = "inline-block";
    }else{
        displayType = "block";
    }
    $(".answer:visible").css("display",displayType);
}