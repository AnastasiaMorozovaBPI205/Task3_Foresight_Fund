import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Tree {
    private static class Node {
        String value;
        ArrayList<Node> children = new ArrayList<>();
        Boolean isLeaf;
        String classifier;

        Node(String value, Boolean isLeaf, String classifier) {
            this.value = value;
            this.isLeaf = isLeaf;
            this.classifier = classifier;
        }
    }

    private final Node root = new Node("root", false, "");
    private final List<String> classifiers;

    public Tree(String ... classifiers) {
        this.classifiers = Arrays.stream(classifiers).toList();
    }

    public void buildTree(String fileName) throws IOException {
        XSSFWorkbook excelBook = new XSSFWorkbook(new FileInputStream(fileName));
        XSSFSheet bookSheet = excelBook.getSheetAt(0);

        for (int i = 1; i <= bookSheet.getLastRowNum(); ++i) {
            XSSFRow row = bookSheet.getRow(i);
            int columnNumber = 0;
            Node lastNode = root;
            int countClassifiers = 0;

            while (countClassifiers != classifiers.size()) {
                // Ищем номер колонки с нужным классификатором.
                for (int j = 1; j < bookSheet.getRow(0).getLastCellNum(); ++j) {
                    if (Objects.equals(bookSheet.getRow(0).getCell(j).getStringCellValue(), classifiers.get(countClassifiers))){
                        ++countClassifiers;
                        columnNumber = bookSheet.getRow(0).getCell(j).getColumnIndex();
                        break;
                    }
                }

                // Находим значение искомой клетки.
                String cellValue = row.getCell(columnNumber).getStringCellValue();

                // Проверяем есть ли уже такой Node в дереве, если да, то продвигаемся дальше, иначе добавляем его в дерево.
                boolean alreadyHaveThisNode = false;
                for (int j = 0; j < lastNode.children.size(); ++j) {
                    if (Objects.equals(lastNode.children.get(j).value, cellValue)) {
                        lastNode = lastNode.children.get(j);
                        alreadyHaveThisNode = true;
                        break;
                    }
                }

                if (!alreadyHaveThisNode) {
                    Node newNode = new Node(cellValue, false, bookSheet.getRow(0).getCell(columnNumber).getStringCellValue());
                    lastNode.children.add(newNode);
                    lastNode = newNode;
                }
            }

            // Добавляем лист.
            lastNode.children.add(new Node(row.getCell(0).getStringCellValue(), true, "leaf"));
        }

        excelBook.close();
    }

    public ArrayList<String> getChildren(StringPair ... pairs) {
        // Если метод getChildren вызван без параметров, то возвращаем все листья дерева.
        if (pairs.length == 0) {
            return getLeaves(root);
        }

        // Если в парах есть элементы с равными классификаторами, но разными классами - возвращаем пустой массив.
        for (int i = 0; i < pairs.length; ++i) {
            for (int j = i + 1; j < pairs.length; ++j) {
                if (Objects.equals(pairs[i].classifier, pairs[j].classifier) && Objects.equals(pairs[i].class_, pairs[j].class_)) {
                    return new ArrayList<>();
                }
            }
        }

        // Сортируем пары в порядке данных классификаторов.
        ArrayList<StringPair> sortedPairs = new ArrayList<>();
        for (String classifier : classifiers) {
            for (StringPair pair : pairs) {
                if (Objects.equals(classifier, pair.classifier)) {
                    sortedPairs.add(pair);
                    break;
                }
            }
        }

        // Проходим по пути, созданным парами и находим Node, листья которого нужно вернуть.
        ArrayList<Node> currentNodes = new ArrayList<>();
        currentNodes.add(root);

        ArrayList<Node> newNodes = new ArrayList<>();

        for (int i = 0; i < sortedPairs.size(); ++i) {
            for (Node node : currentNodes) {
                for (Node child : node.children) {
                    newNodes.addAll(findNode(child, pairs[i].class_, pairs[i].classifier, new ArrayList<>()));
                }
            }

            currentNodes = new ArrayList<>(newNodes);
            newNodes = new ArrayList<>();
        }

        ArrayList<String> leaves = new ArrayList<>();
        for (Node currentNode : currentNodes) {
            leaves.addAll(getLeaves(currentNode));
        }

        return leaves;
    }

    private ArrayList<Node> findNode(Node lastNode, String value, String classifier, ArrayList<Node> nodesToFind) {
        if (Objects.equals(lastNode.value, value) && Objects.equals(lastNode.classifier, classifier)) {
            nodesToFind.add(lastNode);
        }

        for (Node child : lastNode.children) {
            return findNode(child, value, classifier, nodesToFind);
        }

        return nodesToFind;
    }

    public static class StringPair{
        String classifier;
        String class_;
        StringPair(String first, String second) {
            this.classifier = first;
            this.class_ = second;
        }
    }

    private ArrayList<String> getLeaves(Node node) {
        ArrayList<String> children = new ArrayList<>();

        for (Node child : node.children) {
            if (child.isLeaf) {
                children.add(child.value);
            } else {
                children.addAll(getLeaves(child));
            }
        }

        return children;
    }
}
