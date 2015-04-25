<html>
<head>
    <title>${projectName}</title>
    <style>
        body {
            background-color: azure;
        }

        .projectName {
            font-size: 24px;
            text-decoration: underline;
            font-weight: bold;
        }

        .specName {
            font-size: 18px;
            font-weight: bold;
        }

        .scenarioName {
            font-weight: bold;
        }

        .spec {
            margin-left: 3%;
        }

        .scenario {
            margin-left: 5%;
        }

        .className {
            font-style: italic;
        }
    </style>
</head>
<body>
<div class="projectName">${projectName}</div>

<#list specs as spec>

<div class="spec">
    <#if spec.title??>
        <div class="specName">${spec.title}</div>
    <#else>
        <h2 class="specName">${spec.name}</h2>
    </#if>

    <div class="classNameHeader">Test class: <span class="className">${spec.name}</span></div>

    <#if spec.description??>
        <div class="specDescription">${spec.description}</div>
    </#if>

    <#if spec.subjects??>
        <div specSubjects>
            Testing:
            <ul>
                <#list spec.subjects as subject>
                    <li>${subject}</li>
                </#list>
            </ul>
        </div>
    </#if>
    <#if spec.links??>
        <div class="specLinks">
            Links:
            <ul>
                <#list spec.links as link>
                    <li><a href="${link}">${link}</a></li>
                </#list>
            </ul>
        </div>
    </#if>
    <#if spec.issues??>
        <div class="specIssue">
            Issues:
            <ul>
                <#list spec.issues as issue>
                    <li><a href="${issue}">${issue}</a></li>
                </#list>
            </ul>
        </div>
    </#if>

    <#list spec.scenarios as scenario>
        <div class="scenario">
            <div class="scenarioName">${scenario.name}</div>
            <#if scenario.links??>
                <div class="scenarioLinks">
                    Links:
                    <ul>
                        <#list scenario.links as link>
                            <li><a href="${link}">${link}</a></li>
                        </#list>
                    </ul>
                </div>
            </#if>
            <#if scenario.issues??>
                <div class="scenarioIssue">
                    Issues:
                    <ul>
                        <#list scenario.issues as issue>
                            <li><a href="${issue}">${issue}</a></li>
                        </#list>
                    </ul>
                </div>
            </#if>
            <div class="scenarioSteps">
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
        </div>
    </#list>
</div>
</#list>

</body>
</html>