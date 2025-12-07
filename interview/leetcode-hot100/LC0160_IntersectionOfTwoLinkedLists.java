public class LC0160_IntersectionOfTwoLinkedLists {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) {
            return null;
        }
        ListNode pA = headA;
        ListNode pB = headB;
        while (pA != pB) {
            pA = (pA == null) ? headB : pA.next;
            pB = (pB == null) ? headA : pB.next;
        }
        return pA;
    }

    public static void main(String[] args) {
        ListNode c1 = new ListNode(8);
        c1.next = new ListNode(4);
        c1.next.next = new ListNode(5);
        ListNode a1 = new ListNode(4);
        a1.next = new ListNode(1);
        a1.next.next = c1;
        ListNode b1 = new ListNode(5);
        b1.next = new ListNode(0);
        b1.next.next = new ListNode(1);
        b1.next.next.next = c1;
        LC0160_IntersectionOfTwoLinkedLists solver = new LC0160_IntersectionOfTwoLinkedLists();
        System.out.println(solver.getIntersectionNode(a1, b1).val);
    }
}
