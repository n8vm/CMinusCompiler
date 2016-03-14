/*
 * Code formatter project
 * CS 4481
 */
package formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides methods returning registers that are available for use. Registers
 * should be cleared using the clear() method when no longer needed.
 *
 * @author edwajohn
 */
public final class RegisterAllocator {

  // True if t is used
  private final boolean[] t = new boolean[10];
  private final boolean[] s = new boolean[9];
  private final Set<String> used = new HashSet<>();

  public RegisterAllocator() {
    clearAll();
  }

  public String getT() {
    for (int i = 0; i < t.length; ++i) {
      if (!t[i]) {
        t[i] = true;
        String str = "$t" + i;
        used.add(str);
        return str;
      }
    }
    return null;
  }

  public String getS() {
    for (int i = 0; i < s.length; ++i) {
      if (!s[i]) {
        s[i] = true;
        String str = "$s" + i;
        used.add(str);
        return str;
      }
    }
    return null;
  }

  public List<String> getUsed() {
    return new ArrayList<>(used);
  }

  /**
   * Any time you call this method you should seriously consider adding a
   * corresponding clear() call.
   *
   * @return
   */
  public String getAny() {
    String availS = getS();
    if (availS != null) {
      return availS;
    }
    return getT();
  }

  public void clear(String reg) {
    if (reg.charAt(1) == 's') {
      s[Integer.parseInt(reg.substring(2))] = false;
    } else if (reg.charAt(1) == 't') {
      t[Integer.parseInt(reg.substring(2))] = false;
    } else {
      throw new RuntimeException("Unexpected register in clear");
    }
  }

  public void clearAll() {
    Arrays.fill(t, false);
    Arrays.fill(s, false);
  }
}
