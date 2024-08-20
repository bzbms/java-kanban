/*
Привет, Сергей.
Ох, и попил мне крови мне этот двусвязный список. х)
Сидел с картинкой, где рисовал связи и писал список действий, но затыки были в том как перенести с картинки на код.
А когда он вдруг не работал, то пришлось загружать в свой ум всю эту работу и он очень быстро перегревался от
представления и запоминания связей...

Из всех условий ТЗ не очень понимаю, что мне делать с другими сеттерами... Этот вопрос поднимался в группе, но то,
как он решён, оставляет неясности - хотелось бы узнать, что точно нужно делать прежде, чем очень многое переделывать...
А то ТЗ предлагает "подумать" над решениями проблем с ними, а не решить их...?)

*/


package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString() {
            String showNode = "Узел{task=" + task;
            return showNode + '}';
        }
    }

    private final HashMap<Integer, Node> nodesByTaskIds = new HashMap<>();
    private Node firstAddition;
    private Node lastAddition;

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        removeNode(nodesByTaskIds.get(task.getId()));
        addNode(task);
    }

    @Override
    public void removeTask(int id) {
        removeNode(nodesByTaskIds.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(fillHistory());
    }

    private void addNode(Task task) {
        final Node newNode = new Node(lastAddition, task, null);

        if (lastAddition == null) { // Если это 1-е добавление (в последнем ничего не было),
            firstAddition = newNode; // то надо запомнить Узел в голове.
        } else {
            lastAddition.next = newNode; // Иначе предпоследнему Узлу надо занести ссылку на добавляемый
        }
        lastAddition = newNode; // И затем новый становится последним
        nodesByTaskIds.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node shiftNode = nodesByTaskIds.remove(node.task.getId());

        if (shiftNode.next == null && shiftNode.prev == null) { // Если удаляемый Узел - единственный
            firstAddition = null;
            lastAddition = null;
            return;
        }
        if (shiftNode.prev == null) { // Если удаляемый Узел - первый (впереди ничего нет)
            firstAddition = shiftNode.next; // Делаем следующий первым
            shiftNode.next.prev = null; // Впереди у него ничего не должно быть
            return;
        }
        if (shiftNode.next == null) { // Если удаляемый Узел - последний (позади ничего нет)...
            lastAddition = shiftNode.prev; // ... такое может произойти только при удалении задачи через TaskManager...
            shiftNode.prev.next = null; // ... так как одинаковая последняя и не добавляется в историю.
            return;
        }
        // Если посередине (все условия исчерпаны):
        shiftNode.prev.next = shiftNode.next; // То в его предыдущем записываем ссылку на последующий
        shiftNode.next.prev = shiftNode.prev; // А в последующем - ссылку на предыдущий
    }

    private ArrayList<Task> fillHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = firstAddition; // Обращаемся к первому Узлу списка
        while (node != null) { // Пока Узел не пустой
            tasks.add(node.task); // Добавляем Задачи из Узлов в список вывода
            node = node.next; // И переходим на следующий Узел, реализуя так порядок добавления
        }
        return tasks;
    }
}
