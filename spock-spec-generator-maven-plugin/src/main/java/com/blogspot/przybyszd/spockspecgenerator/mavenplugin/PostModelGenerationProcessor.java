package com.blogspot.przybyszd.spockspecgenerator.mavenplugin;

import com.blogspot.przybyszd.spockspecgenerator.core.domain.Block;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Scenario;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Spec;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Statement;

import java.util.ArrayList;
import java.util.List;

class PostModelGenerationProcessor {

    private final boolean omitBlocksWithoutDescription;
    private final List<Block> omitBlocks;
    private final boolean mergeAndBlock;

    public PostModelGenerationProcessor(boolean omitBlocksWithoutDescription, List<Block> omitBlocks, boolean mergeAndBlock) {
        this.omitBlocksWithoutDescription = omitBlocksWithoutDescription;
        this.omitBlocks = omitBlocks;
        this.mergeAndBlock = mergeAndBlock;
    }

    List<Spec> applyParameterToSpecs(List<Spec> specs) {
        List<Spec> newSpecs = new ArrayList<>();
        for (Spec spec : specs) {
            Spec newSpec = new Spec(
                    spec.getName(),
                    spec.getTitle(),
                    spec.getDescription(),
                    spec.getSubjects(),
                    applyParameterToScenarios(spec.getScenarios()),
                    spec.getIssues(),
                    spec.getLinks());
            newSpecs.add(newSpec);
        }
        return newSpecs;
    }

    private List<Scenario> applyParameterToScenarios(List<Scenario> scenarios) {
        List<Scenario> newScenarios = new ArrayList<>();
        for (Scenario scenario : scenarios) {
            Scenario newScenario = new Scenario(
                    scenario.getName(),
                    applyParameterToStatements(scenario.getStatements()),
                    scenario.getIssues(),
                    scenario.getLinks()
            );
            newScenarios.add(newScenario);
        }
        return newScenarios;
    }

    private List<Statement> applyParameterToStatements(List<Statement> statements) {
        List<Statement> newStatements = new ArrayList<>();
        for (final Statement currentStatement : statements) {
            if (omitBlocksWithoutDescription && currentStatement.getDescription() == null) {
                continue;
            }
            if (omitBlocks.contains(currentStatement.getBlock())) {
                continue;
            }
            if (shouldMergeAndBlock(newStatements, currentStatement)) {
                final int lastStatementIndex = newStatements.size() - 1;
                Statement lastStatement = newStatements.get(lastStatementIndex);
                String newDescription = mergeDescriptionsWithAnd(currentStatement, lastStatement);
                Statement newStatement = new Statement(lastStatement.getBlock(), newDescription);
                newStatements.set(lastStatementIndex, newStatement);
            } else {
                newStatements.add(currentStatement);
            }
        }
        return newStatements;
    }

    private String mergeDescriptionsWithAnd(Statement currentStatement, Statement lastStatement) {
        List<String> description = new ArrayList<>();
        if (lastStatement.getDescription() != null) {
            description.add(lastStatement.getDescription());
        }
        if (currentStatement.getDescription() != null) {
            description.add(currentStatement.getDescription());
        }
        if (description.size() == 2) {
            return description.get(0) + " and " + description.get(1);
        } else if (description.size() == 1) {
            return description.get(1);
        }
        return null;
    }

    private boolean shouldMergeAndBlock(List<Statement> newStatements, Statement currentStatement) {
        return mergeAndBlock
                && currentStatement.getBlock() == Block.AND
                && !newStatements.isEmpty();
    }
}
