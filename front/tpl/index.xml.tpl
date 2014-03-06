<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet href="/xsl/index.xsl" type="text/xsl"?>
<page>
   <circles>
        % for circle in circles:
            <circle id='{{circle['id']}}'>
                <city>{{circle['city']}}</city>
                <tag>{{circle['tag']}}</tag>
            </circle>
        % end
    </circles>
</page>
