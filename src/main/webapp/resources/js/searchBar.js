/**
 * Created by Bogdan.Stefan on 12/29/2014.
 */

function searchInputTop() {

    document.getElementById("searchInput").addEventListener("keyup", function () {

        console.log("typing");

        var wrapper = document.getElementById("contentWrapper");
        wrapper.className = "container-fluid";

        var form = document.getElementById("searchForm");
        form.className = "margin-top-xxs-1";

        var div_img = document.getElementById("logoImg");
        div_img.className= "row setImageToTop";

        var img = document.getElementById("SemanticWebSearchImg");
        img.className = "img-responsive center-block";

        var sOptions = document.getElementById("sOptions");
        sOptions.className = "margin-top-xxs-8";

        var displayAs = document.getElementById("displayAs");
        displayAs.className = "max-width-50 float-right row";

    }, false);

}
