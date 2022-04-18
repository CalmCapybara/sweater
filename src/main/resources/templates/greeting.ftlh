<#import "parts/common.ftlh" as c>

<@c.page>
    <#assign
    known = Session.SPRING_SECURITY_CONTEXT??
    >
    <#if known>
        <#assign
        user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
        name = user.getUsername()
        >
    <#else>
        <#assign
        name = "guest"
        >
    </#if>
    <h5>Hello, ${name}</h5>
    <div>This is a simple clone off Twitter</div>
</@c.page>

