/**
 * Created by Bogdan.Stefan on 2/4/2015.
 */
var detach = true;

$('#search').on('input',function(){
    //execute only once !
    if(detach){

        detach = false;

        $("#topHeader").addClass("topHeader");
        $("#topBar").removeClass("mid-bar");
        $("#topBar").addClass("top-bar");

        $(".wrap").removeClass("col-xs-70");
        $("#display-as").removeClass("no-display");
        $("#toggler").removeClass("no-display");

        $("#option-conntent").addClass("no-display");
        $(".mid-logo").addClass("no-display");
    }
});

$('#toggle').on('click',function(){
    $("#option-conntent").toggle('fast');
});