public class LC0004_MedianOfTwoSortedArrays {

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }
        int m = nums1.length;
        int n = nums2.length;
        int totalLeft = (m + n + 1) / 2;
        int left = 0, right = m;
        while (left <= right) {
            int i = (left + right) / 2;
            int j = totalLeft - i;

            int nums1LeftMax = (i == 0) ? java.lang.Integer.MIN_VALUE : nums1[i - 1];
            int nums1RightMin = (i == m) ? java.lang.Integer.MAX_VALUE : nums1[i];
            int nums2LeftMax = (j == 0) ? java.lang.Integer.MIN_VALUE : nums2[j - 1];
            int nums2RightMin = (j == n) ? java.lang.Integer.MAX_VALUE : nums2[j];

            if (nums1LeftMax <= nums2RightMin && nums2LeftMax <= nums1RightMin) {
                if (((m + n) & 1) == 1) {
                    return java.lang.Math.max(nums1LeftMax, nums2LeftMax);
                } else {
                    return (java.lang.Math.max(nums1LeftMax, nums2LeftMax)
                            + java.lang.Math.min(nums1RightMin, nums2RightMin)) / 2.0;
                }
            } else if (nums1LeftMax > nums2RightMin) {
                right = i - 1;
            } else {
                left = i + 1;
            }
        }
        return 0.0;
    }

    public static void main(String[] args) {
        LC0004_MedianOfTwoSortedArrays solver = new LC0004_MedianOfTwoSortedArrays();
        System.out.println(solver.findMedianSortedArrays(new int[]{1, 3}, new int[]{2}));
        System.out.println(solver.findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4}));
    }
}
