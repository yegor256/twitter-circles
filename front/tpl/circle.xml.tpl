<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet href="/xsl/circle.xsl" type="text/xsl"?>
<page>
    <circle>
        <id>{{circle['id']}}</id>
        <city>{{circle['city']}}</city>
        <tag>{{circle['tag']}}</tag>
    </circle>
    <ranks>
        % for rank in ranks:
            <rank>
                <user>{{rank['user']}}</user>
                <value>{{rank['value']}}</value>
                <link rel='twitter' href='http://www.twitter.com/{{rank['user']}}'/>
                <link rel='spam' href='/spam/{{circle['id']}}/{{rank['id']}}'/>
            </rank>
        % end
    </ranks>
</page>
