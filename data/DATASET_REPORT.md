# Graph Dataset Report

This report describes the 9 generated datasets for testing graph algorithms.

## Dataset Summary

| Category | Dataset | Nodes | Edges | Source | Description |
|----------|---------|-------|-------|--------|-------------|
| Small | small1.json | 7 | 6 | 3 | Simple case with 1 cycle and DAG structure (Cyclic) |
| Small | small2.json | 7 | 7 | 1 | Pure DAG with multiple paths (Acyclic) |
| Small | small3.json | 6 | 8 | 2 | Multiple small cycles with connections (Cyclic) |
| Medium | medium1.json | 14 | 16 | 6 | Mixed structure with several SCCs (Cyclic) |
| Medium | medium2.json | 18 | 45 | 12 | Dense cyclic graph with DAG components (Cyclic) |
| Medium | medium3.json | 12 | 16 | 8 | Sparse DAG with complex dependencies (Acyclic) |
| Large | large1.json | 25 | 67 | 9 | Large mixed graph for performance testing (Cyclic) |
| Large | large2.json | 35 | 210 | 15 | Dense DAG for critical path analysis (Acyclic) |
| Large | large3.json | 40 | 58 | 20 | Complex graph with multiple SCC hierarchies (Cyclic) |

## Dataset Details

### small1.json

- **Category**: Small
- **Nodes**: 7
- **Edges**: 6
- **Source**: 3
- **Type**: Cyclic
- **Description**: Simple case with 1 cycle and DAG structure

### small2.json

- **Category**: Small
- **Nodes**: 7
- **Edges**: 7
- **Source**: 1
- **Type**: Acyclic
- **Description**: Pure DAG with multiple paths

[... остальные датасеты ...]

## Usage

These datasets are used for testing:
- Strongly Connected Components (SCC) detection
- Topological ordering of DAGs
- Shortest paths in DAGs
- Critical path analysis