angular.module('adam').config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/help");
    $stateProvider
        .state('help', {
            url: "/help",
            templateUrl: "app/components/help/HelpView.html"
        })
        .state('finder', {
            url: "/finder",
            templateUrl: "app/components/finder/FinderView.html"
        })
        .state('log', {
            url: "/log",
            templateUrl: "app/components/log/LogView.html"
        });
});