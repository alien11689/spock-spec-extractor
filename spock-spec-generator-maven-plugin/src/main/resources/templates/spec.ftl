<html>
<head>
    <title>${projectName}</title>
</head>
<body>
<h1>${projectName}</h1>

<#list specs as spec>

<div>
    <#if spec.title??>
        <h2>${spec.title}</h2>
    <#else>
        <h2>${spec.name}</h2>
    </#if>

    <div>Test class: <i>${spec.name}</i></div>

    <#if spec.description??>
        <p>${spec.description}</p>
    </#if>

    <#if spec.subjects??>
        <div>
            Testing:
            <ul>
                <#list spec.subjects as subject>
                    <li>${subject}</li>
                </#list>
            </ul>
        </div>
    </#if>
    <#if spec.links??>
        <div>
            Links:
            <ul>
                <#list spec.links as link>
                    <li><a href="${link}">${link}</a></li>
                </#list>
            </ul>
        </div>
    </#if>
    <#if spec.issues??>
        <div>
            Issues:
            <ul>
                <#list spec.issues as issue>
                    <li><a href="${issue}">${issue}</a></li>
                </#list>
            </ul>
        </div>
    </#if>

    <#list spec.scenarios as scenario>
        <div>
            <h3>${scenario.name}</h3>
            <#if scenario.links??>
                <div>
                    Links:
                    <ul>
                        <#list scenario.links as link>
                            <li><a href="${link}">${link}</a></li>
                        </#list>
                    </ul>
                </div>
            </#if>
            <#if scenario.issues??>
                <div>
                    Issues:
                    <ul>
                        <#list scenario.issues as issue>
                            <li><a href="${issue}">${issue}</a></li>
                        </#list>
                    </ul>
                </div>
            </#if>
            <ol>
                <#list scenario.statements as statement>
                    <li>
                    ${statement.block.capitalized()}
                        <#if statement.description??>
                    ${statement.description}
                    </#if>
                    </li>
                </#list>
            </ol>
        </div>
    </#list>
</#list>

</div>

</body>
</html>