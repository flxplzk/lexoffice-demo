(function () {
    var cage = angular.module("de.nordakademie.iaa.survey.cage", ["ngMaterial"]);
    cage.directive("whatWouldNicolasSay", CageDirective);
    var cages = ["<iframe src='https://giphy.com/embed/xTiTnC5cMmUx9bfWYU' width='480' height='360' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/HioOq6DyB17BC' width='480' height='360' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/lftGjtWv91Teo' width='480' height='204' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/LXtjHzZjC5WLu' width='480' height='480' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/bn0zlGb4LOyo8' width='480' height='451' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/KWzzTbkhDvmQU' width='480' height='204' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/HNQVf0ik57nHy' width='480' height='261' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/3YGKFfw611fZS' width='480' height='475' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/13NG1gPqHEJOuY' width='360' height='480' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/RrVzUOXldFe8M' width='480' height='360' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/MYkPISup4TF60' width='480' height='328' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/MYkPISup4TF60' width='480' height='328' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src='https://giphy.com/embed/QQZgTehcKrD2w' width='480' height='480' frameBorder='0' class='giphy-embed' allowFullScreen></iframe>",
        "<iframe src=\"https://giphy.com/embed/46jocVlIL8Vpu\" width=\"480\" height=\"204\" frameBorder=\"0\" class=\"giphy-embed\" allowFullScreen></iframe>"];

    function CageDirective() {
        var random = Math.floor(Math.random() * Math.floor(cages.length - 1));
        return {
            restrict: "E",
            template: cages[random]
        }
    }
})();
