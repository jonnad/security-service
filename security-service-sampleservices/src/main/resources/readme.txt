To test servlet access (http GET and POST) via proxy...

http://localhost:8980/sampleservices/businessServlet

To test it without the proxy (to ensure it is working), use...

http://localhost/sampleservices/businessServlet (GET via browser)
http://localhost/sampleservices/businessServlet (POST via Poster via Firefox)

To test behind the proxy, go to...

http://localhost:8980/sampleservices/businessServlet

You should receive a 403 - Unauthorized status from the proxy


To get by the proxy, you need to have a valid security token header sent along with your request.

s_token=PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48c2FtbDJwOlJlc3BvbnNlIHhtb...==

POST or GET via Poster can append the header field, but you must get the token from the security API (documented a bit later)


## Security API ##

