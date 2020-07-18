module.exports = function (app) {
    app.get('/users', (request, response) => {
        var result = [{
            "name": "Anastasia",
            "surname": "Semenova"

        , "skills": [
                "Понимание ООП; ",

                "Знания JavaCore, testing, multithreading; ",

                "Знание английского на разговорном уровне. "
            ]}];
        response.send(JSON.stringify(result));
    });
};
