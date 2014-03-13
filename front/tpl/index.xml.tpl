<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet href="/xsl/index.xsl" type="text/xsl"?>
<page>
    <latest>{{latest}}</latest>
    <circles>
        % for circle in circles:
            <circle id='{{circle['id']}}'>
                <city>{{circle['city']}}</city>
                <tag>{{circle['tag']}}</tag>
                <tweets>{{circle['sum']}}</tweets>
                <link rel='see' href='/circle/{{circle['id']}}'/>
                <link rel='delete' href='/delete/{{circle['id']}}'/>
            </circle>
        % end
    </circles>
</page>
