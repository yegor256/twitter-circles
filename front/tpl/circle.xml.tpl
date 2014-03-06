<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet href="/xsl/circle.xsl" type="text/xsl"?>
<page>
   <ranks>
        % for rank in ranks:
            <rank>
                <user>{{rank['user']}}</user>
                <value>{{rank['value']}}</value>
            </rank>
        % end
    </ranks>
</page>
