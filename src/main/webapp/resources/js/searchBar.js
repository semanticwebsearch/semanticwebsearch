/**
 * Created by Bogdan.Stefan on 12/29/2014.
 */

function searchInputTop() {

    document.getElementById("searchInput").addEventListener("keyup", function () {

        this.className = "form-control setSearchBarTop";
        console.log("typing");

        var img = document.getElementById("SemanticWebSearchImg");
        img.className= "setImageToTop";
    }, false);


}
