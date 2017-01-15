### References

[Best Practices for Designing a Pragmatic RESTful API](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api)

[Thoughts on RESTful API Design](https://restful-api-design.readthedocs.org/en/latest)

[Design and Build RESTful API with Spring HATEOAS](https://www.jiwhiz.com/blogs/Design_and_Build_RESTful_API_with_Spring_HATEOAS)

[HAL JSON Specification](http://stateless.co/hal_specification.html)

[Link Relations](https://www.iana.org/assignments/link-relations/link-relations.xhtml)

### Headers

x-loginId

### URL Parameters

partnerId

##### GET (read)

/users/prefs?partnerId={partnerId}
200

```

{
    "_links": {
        "self": {
            "href": "/users/prefs/{name}/svc/{serviceId}?partnerId={partnerId}",
            "templated": true
        }
    }
}

```

http://localhost:8080/users/prefs/favoriteCity/svc/1
200 or 404

```

{
    "name": "favoriteCity",
    "value": "seattle",
    "serviceId": 1,
    "links": [
        {
            "rel": "self",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1"
        },
        {
            "rel": "update",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1?value=seattle"
        },
        {
            "rel": "delete",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1"
        }
    ]
}

```

##### POST (create)

http://localhost:8080/users/prefs/favoriteCity/svc/1?value=seattle

201 or 409

```

{
    "name": "favoriteCity",
    "value": "seattle",
    "serviceId": 1,
    "links": [
        {
            "rel": "self",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1"
        },
        {
            "rel": "update",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1?value=seattle"
        },
        {
            "rel": "delete",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1"
        }
    ]
}

```

##### PATCH (update)

http://localhost:8080/users/prefs/favoriteCity/svc/1?value=sfo
200 or 404


```

{
    "name": "favoriteCity",
    "value": "sfo",
    "serviceId": 1,
    "links": [
        {
            "rel": "self",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1"
        },
        {
            "rel": "update",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1?value=seattle"
        },
        {
            "rel": "delete",
            "href": "http://localhost:8080/users/prefs/favoriteCity/svc/1"
        }
    ]
}

```

##### DELETE (delete)

http://localhost:8080/users/prefs/favoriteCity/svc/1
204 or 404

---

SERVICES
-----------------
id (PK) | name

USERS
-----------------
username (PK) | partner_id


USER_PREFS
-----------------
name(PK) | val | service_id (PK, FK) | username (PK, FK)	


