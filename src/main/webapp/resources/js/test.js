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

            lookingFor();

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

    /* do the search */

    /* set up the layout*/
    detacheSearchBar();
});

function detacheSearchBar(){
    if(!($("#toggled_content").find("#sOptions").length == 1)) {

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

        console.log("detache sOption bar");

        $("#sOptions").detach().appendTo("#toggled_content");

        $("#displayAs").removeClass("no-display");
        $("#sOptions").removeClass("row");
        $("hr").removeClass("no-display");
    }
    lookingFor();
}

//looking for : check what am i looking for and set the list or gread layout for display of answers

 function lookingFor(){

      var groupt = $("#lookFor input:checkbox");
      var count = 0;
     console.log(groupt);
      $.each(groupt, function(index, value){

          console.log('lookingFor : '+value.name);
          if(value.checked){
              count +=1;
          }

      });
      console.log('lookingFor count :'+count);

      if(count == 0){
          //load grid
          //set all img to checked
          $("#lookFor input:checkbox").attr('checked',true);
          setLayout("grid");

      }else if(count == 1){
          //load list
          setLayout("list");

        }else{
            //load grid
          setLayout("grid");

        }
  };

function setLayout(name){

    var group = $('#displayAs input:checkbox');
    var displayAs = '#displayAs input:checkbox#'+name;
    if($(displayAs).length > 0) {
        console.log($(displayAs));
        $("#mainLayout").attr("href", $(displayAs).attr('rel'));

        group.attr("checked",false);
        $(displayAs).attr("checked",true);
    }else{
        console.log("[setLayout(name)] there is no id :" + name);
    }
}

$('.toggler').on('click',function(){

    $(this).parent().children().toggle();  //swaps the display:none between the two spans
    $(this).parent().parent().find('#toggled_content').slideToggle('fast');  //swap the display of the main content with slide action
});

$('#lookFor div label').on('click',function(){

    var name =$(this).parent().find('input').attr('name');
    console.log('[#lookFor div]  > clicking '+ name);
    var answer = ".answer.answer-"+ name;

    if($(this).parent().find('input').get(0).checked){
        console.log(name+' was checked');
        console.log(answer);
        $(answer).fadeOut('fast');
    }else{
        console.log(name+' was\'nt checked');
        //testeaza daca exista acele date ....
        //daca nu exista trebuie aduse altfel ->
        $(answer).fadeIn('slow').css("display","inline-block");
    }


});


//swap the css format for answers display
$("input:checkbox").click(function(){

    if(this.name == "displayOpt") {

        var group = "input:checkbox[name='" + $(this).attr("name") + "']";

        $(group).attr("checked",false);
        $(this).attr("checked",true);

        $("#mainLayout").attr("href",$(this).attr('rel'));
    }
});