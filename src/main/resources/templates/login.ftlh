<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>
    <#if errorMessage??>
        <div class="alert alert-danger" role="alert">
<#--            ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}-->
<#--            <#if errorMessage??>${errorMessage}</#if>-->
            ${errorMessage}
        </div>
    </#if>
    <#if message??>
        <div class="alert alert-${messageType}" role="alert">
            ${message}
        </div>
    </#if>
    <@l.login "/login" false/>
</@c.page>
