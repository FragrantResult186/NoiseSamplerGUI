package nl.jellejurre.biomesampler.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import nl.kallestruik.noisesampler.minecraft.util.NoiseValuePoint;
import nl.kallestruik.noisesampler.minecraft.util.Pair;

@SuppressWarnings("ALL")
public class MultiNoiseUtil {
    public static NoiseHypercube createNoiseHypercube(float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, float offset) {
        return new NoiseHypercube(ParameterRange.of(temperature), ParameterRange.of(humidity), ParameterRange.of(continentalness), ParameterRange.of(erosion), ParameterRange.of(depth), ParameterRange.of(weirdness), convertFloatToLong(offset));
    }

    public static NoiseHypercube createNoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, float offset) {
        return new NoiseHypercube(temperature, humidity, continentalness, erosion, depth, weirdness, convertFloatToLong(offset));
    }

    public static long convertFloatToLong(float f) {
        return (long)(f * 10000.0F);
    }

    public record NoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, long offset) {
        private List<ParameterRange> getParameters() {
            return List.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new ParameterRange(this.offset, this.offset));
        }

        public long offset() {
            return this.offset;
        }
    }

    public record ParameterRange(long min, long max) {
        public static ParameterRange of(float point) {
            return of(point, point);
        }

        public static ParameterRange of(float min, float max) {
            if (min > max) {
                throw new IllegalArgumentException("min > max: " + min + " " + max);
            } else {
                return new ParameterRange(MultiNoiseUtil.convertFloatToLong(min), MultiNoiseUtil.convertFloatToLong(max));
            }
        }

        public static ParameterRange combine(ParameterRange min, ParameterRange max) {
            if (min.min() > max.max()) {
                throw new IllegalArgumentException("min > max: " + min + " " + max);
            } else {
                return new ParameterRange(min.min(), max.max());
            }
        }

        public long getDistance(long noise) {
            long l = noise - this.max;
            long m = this.min - noise;
            return l > 0L ? l : Math.max(m, 0L);
        }

        public ParameterRange combine(ParameterRange other) {
            return other == null ? this : new ParameterRange(Math.min(this.min, other.min()), Math.max(this.max, other.max()));
        }

        public long min() {
            return this.min;
        }

        public long max() {
            return this.max;
        }
    }

    public static class Entries<T> {
        private final SearchTree<T> tree;

        public Entries(List<Pair<NoiseHypercube, T>> entries) {
            this.tree = SearchTree.create(entries);
        }

        public T getNoiseValue(NoiseValuePoint noiseValuePoint) {
            return this.getNoiseValueWithDistance(noiseValuePoint, SearchTree.TreeNode::getSquaredDistance);
        }

        protected T getNoiseValueWithDistance(NoiseValuePoint noiseValuePoint, NodeDistanceFunction<T> nodeDistanceFunction) {
            return this.tree.get(noiseValuePoint, nodeDistanceFunction);
        }
    }

    protected static final class SearchTree<T> {
        private final TreeNode<T> firstNode;
        private final ThreadLocal<TreeLeafNode<T>> previousResultNode = new ThreadLocal();

        private SearchTree(TreeNode<T> firstNode) {
            this.firstNode = firstNode;
        }

        public static <T> SearchTree<T> create(List<Pair<NoiseHypercube, T>> entries) {
            if (entries.isEmpty()) {
                throw new IllegalArgumentException("Need at least one value to build the search tree.");
            } else {
                int i = ((NoiseHypercube)((Pair)entries.get(0)).getLeft()).getParameters().size();
                if (i != 7) {
                    throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
                } else {
                    List<TreeLeafNode<T>> list = (List)entries.stream().map((entry) -> new TreeLeafNode(entry.getLeft(), entry.getRight())).collect(Collectors.toCollection(ArrayList::new));
                    return new SearchTree(createNode(i, list));
                }
            }
        }

        private static <T> TreeNode<T> createNode(int parameterNumber, List<? extends TreeNode<T>> subTree) {
            if (subTree.isEmpty()) {
                throw new IllegalStateException("Need at least one child to build a node");
            } else if (subTree.size() == 1) {
                return subTree.get(0);
            } else if (subTree.size() <= 6) {
                subTree.sort(Comparator.comparingLong((node) -> {
                    long l = 0L;
                    for(int j = 0; j < parameterNumber; ++j) {
                        ParameterRange parameterRange = node.parameters[j];
                        l += Math.abs((parameterRange.min() + parameterRange.max()) / 2L);
                    }
                    return l;
                }));
                return new TreeBranchNode(subTree);
            } else {
                long l = 9223372036854775807L;
                int i = -1;
                List<TreeBranchNode<T>> list = null;
                for(int j = 0; j < parameterNumber; ++j) {
                    sortTree(subTree, parameterNumber, j, false);
                    List<TreeBranchNode<T>> list2 = getBatchedTree(subTree);
                    long m = 0L;
                    TreeBranchNode treeBranchNode;
                    for(Iterator var10 = list2.iterator(); var10.hasNext(); m += getRangeLengthSum(treeBranchNode.parameters)) {
                        treeBranchNode = (TreeBranchNode)var10.next();
                    }
                    if (l > m) {
                        l = m;
                        i = j;
                        list = list2;
                    }
                }
                sortTree(list, parameterNumber, i, true);
                return new TreeBranchNode(list.stream().map((node) -> createNode(parameterNumber, Arrays.asList(node.subTree))).collect(Collectors.toList()));
            }
        }

        private static <T> void sortTree(List<? extends TreeNode<T>> subTree, int parameterNumber, int currentParameter, boolean abs) {
            Comparator<TreeNode<T>> comparator = createNodeComparator(currentParameter, abs);
            for(int i = 1; i < parameterNumber; ++i) {
                comparator = comparator.thenComparing(createNodeComparator((currentParameter + i) % parameterNumber, abs));
            }
            subTree.sort(comparator);
        }

        private static <T> Comparator<TreeNode<T>> createNodeComparator(int currentParameter, boolean abs) {
            return Comparator.comparingLong((treeNode) -> {
                ParameterRange parameterRange = treeNode.parameters[currentParameter];
                long l = (parameterRange.min() + parameterRange.max()) / 2L;
                return abs ? Math.abs(l) : l;
            });
        }

        private static <T> List<TreeBranchNode<T>> getBatchedTree(List<? extends TreeNode<T>> nodes) {
            List<TreeBranchNode<T>> list = new ArrayList<>();
            List<TreeNode<T>> list2 = new ArrayList<>();
            int i = (int)Math.pow(6.0D, Math.floor(Math.log((double)nodes.size() - 0.01D) / Math.log(6.0D)));

            for (TreeNode<T> node : nodes) {
                list2.add(node);
                if (list2.size() >= i) {
                    list.add(new TreeBranchNode(list2));
                    list2 = new ArrayList<>();
                }
            }
            if (!list2.isEmpty()) {
                list.add(new TreeBranchNode(list2));
            }
            return list;
        }

        private static long getRangeLengthSum(ParameterRange[] parameters) {
            long l = 0L;
            for (ParameterRange parameterRange : parameters) {
                l += Math.abs(parameterRange.max() - parameterRange.min());
            }
            return l;
        }

        static <T> List<ParameterRange> getEnclosingParameters(List<? extends TreeNode<T>> subTree) {
            if (subTree.isEmpty()) {
                throw new IllegalArgumentException("SubTree needs at least one child");
            } else {
                List<ParameterRange> list = new ArrayList<>();
                for(int j = 0; j < 7; ++j) {
                    list.add(null);
                }
                for (TreeNode<T> tTreeNode : subTree) {
                    for (int k = 0; k < 7; ++k) {
                        list.set(k, tTreeNode.parameters[k].combine(list.get(k)));
                    }
                }
                return list;
            }
        }

        public T get(NoiseValuePoint point, NodeDistanceFunction<T> distanceFunction) {
            long[] ls = new long[]{point.temperatureNoise(), point.humidityNoise(), point.continentalnessNoise(), point.erosionNoise(), point.depth(), point.weirdnessNoise(), 0L};
            TreeLeafNode<T> treeLeafNode = this.firstNode.getResultingNode(ls, this.previousResultNode.get(), distanceFunction);
            this.previousResultNode.set(treeLeafNode);
            return treeLeafNode.value;
        }

        abstract static class TreeNode<T> {
            protected final ParameterRange[] parameters;

            protected TreeNode(List<ParameterRange> parameters) {
                this.parameters = parameters.toArray(new ParameterRange[0]);
            }

            protected abstract TreeLeafNode<T> getResultingNode(long[] otherParameters, TreeLeafNode<T> alternative, NodeDistanceFunction<T> distanceFunction);

            protected long getSquaredDistance(long[] otherParameters) {
                long l = 0L;
                for(int i = 0; i < 7; ++i) {
                    l += MathHelper.square(this.parameters[i].getDistance(otherParameters[i]));
                }
                return l;
            }

            public String toString() {
                return Arrays.toString(this.parameters);
            }
        }

        private static final class TreeBranchNode<T> extends TreeNode<T> {
            final TreeNode<T>[] subTree;

            private TreeBranchNode(List<? extends TreeNode<T>> list) {
                this(SearchTree.getEnclosingParameters(list), list);
            }

            private TreeBranchNode(List<ParameterRange> parameters, List<? extends TreeNode<T>> subTree) {
                super(parameters);
                this.subTree = (TreeNode[])subTree.toArray(new TreeNode[0]);
            }

            protected TreeLeafNode<T> getResultingNode(long[] otherParameters, TreeLeafNode<T> alternative, NodeDistanceFunction<T> distanceFunction) {
                long l = alternative == null ? 9223372036854775807L : distanceFunction.getDistance(alternative, otherParameters);
                TreeLeafNode<T> treeLeafNode = alternative;
                for (TreeNode<T> treeNode : this.subTree) {
                    long m = distanceFunction.getDistance(treeNode, otherParameters);
                    if (l > m) {
                        TreeLeafNode<T> treeLeafNode2 = treeNode.getResultingNode(otherParameters, treeLeafNode, distanceFunction);
                        long n = treeNode == treeLeafNode2 ? m : distanceFunction.getDistance(treeLeafNode2, otherParameters);
                        if (l > n) {
                            l = n;
                            treeLeafNode = treeLeafNode2;
                        }
                    }
                }
                return treeLeafNode;
            }
        }

        private static final class TreeLeafNode<T> extends TreeNode<T> {
            final T value;

            TreeLeafNode(NoiseHypercube parameters, T value) {
                super(parameters.getParameters());
                this.value = value;
            }

            protected TreeLeafNode<T> getResultingNode(long[] otherParameters,  TreeLeafNode<T> alternative, NodeDistanceFunction<T> distanceFunction) {
                return this;
            }
        }
    }

    interface NodeDistanceFunction<T> {
        long getDistance(SearchTree.TreeNode<T> node, long[] ls);
    }
}