define([], function() {
    var myClass = function(test) {
        this.name = test;
    };

    myClass.prototype.showName = function() {
        return this.name;
    };

    return myClass;
});
