package gallop;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class Evaluator {
	private static final byte NAIVE = 0;
	private static final byte SUCCESS = 1;
	private static final byte STARTED = 2;
	private static final byte FAILURE = 3;
	private static final byte STARTFAIL = 4;
	private final int T;
	private int cost;
	private int L;
	private int t;
	private byte status;
	private IntFunction<Integer> a;
	private IntFunction<Integer> b;
	private int aLen;
	private int bLen;
	private boolean aSmall;

	public Evaluator(int t) {
		T = t;
	}

	private int G(int n, int max, boolean std) {
		if (max == 1) {
			return 0;
		} else if (n == 1) {
			return 1;
		}
		int min = 2;
		int res = 1;
		while (min < n) {
			min *= 2;
			res++;
		}
		if (min < max) {
			return 2 * res;
		}
		min = min / 2 + 1;
		while (min < max) {
			int mid = (max + min + (std == aSmall ? 0 : -1)) / 2;
			if (n <= mid) {
				max = mid;
			} else {
				min = mid + 1;
			}
			res++;
		}
		return res;
	}

	private int eval1(int a) {
		return G(a + 1, a + 1, true);
	}

	public int evaluate(List<Integer> run1, List<Integer> run2) {
		L = run1.size();
		if (L == 1) {
			return eval1(run1.get(0));
		}
		t = T;
		aLen = IntStream.range(0, L).map(run1::get).sum();
		bLen = IntStream.range(0, L).map(run2::get).sum();
		aSmall = true;
		cost = G(run1.get(0) + 1, aLen + 1, true) + G(run2.get(L - 1) + 1, bLen + 1, false);
		aLen -= run1.get(0);
		bLen -= run2.get(L - 1);
		if (aLen <= bLen) {
			a = i -> i == 0 ? 0 : run1.get(i);
			b = i -> run2.get(i);
			status = NAIVE;
		} else {
			a = i -> run1.get(L - 1 - i);
			b = i -> run2.get(L - 2 - i);
			aSmall = false;
			status = STARTFAIL;
		}
		int end = (aLen <= bLen) ? L - 2 : L - 3;
		for (int i = 0; i < end; i++) {
			eval(a.apply(i), b.apply(i), false);
		}
		if (aSmall && a.apply(end + 1) == 1) {
			lastEval(a.apply(end));
		} else if (aSmall || b.apply(end + 1) == 1) {
			eval(a.apply(end), b.apply(end), true);
		} else {
			eval(a.apply(end), b.apply(end), false);
			lastEval(a.apply(end + 1));
		}
		return cost;
	}

	private void eval(int a, int b, boolean last) {
		switch (status) {
		case FAILURE:
			t = Math.max(t + 1, 2);
		case STARTFAIL:
		case NAIVE:
			naive(a, b, last);
			break;
		case SUCCESS:
			t--;
			gallop(a, b, last);
			break;
		case STARTED:
			a++;
			aLen++;
			gallop(a, b, last);
		}
	}

	private void naive(int a, int b, boolean last) {
		int s = status == FAILURE || status == STARTFAIL ? t + 1 : t;
		int u = a == 0 ? t + 1 : t;
		if (a >= s) {
			cost += s - 1 + G(a - s + 1, aLen - s + 1, true) + (last && b == 1 ? 0 : G(b, bLen, true));
			status = a >= s + 7 || b >= 8 ? SUCCESS : FAILURE;
		} else if (last && b == u + 1) {
			cost += a + b - 1;
		} else if (b > u) {
			cost += a + u + G(b - u, bLen - u, true);
			status = b >= u + 8 ? SUCCESS : FAILURE;
		} else if (b == u) {
			cost += a + b + -1;
			status = STARTED;
		} else {
			cost += a + b + (last ? -1 : 0);
			status = NAIVE;
		}
		aLen -= a;
		bLen -= b;
	}

	private void gallop(int a, int b, boolean last) {
		cost += G(a, aLen, true) + (b == 1 && last ? 0 : G(b, bLen, true));
		status = a >= 8 || b >= 8 ? SUCCESS : FAILURE;
		aLen -= a;
		bLen -= b;
	}

	private void lastEval(int a) {
		if (a == 1 && status != STARTED) {
			return;
		}
		switch (status) {
		case FAILURE:
			t = Math.max(t + 1, 2);
		case NAIVE:
			int s = status == FAILURE ? t + 1 : t;
			cost += a - 1 >= s ? (s - 1 + G(a - s + 1, aLen - s + 1, true)) : a - 1;
			break;
		case SUCCESS:
			t--;
			cost += G(a, aLen, true);
			break;
		case STARTED:
			a++;
			aLen++;
			cost += G(a, aLen, true);
		}
	}
}
