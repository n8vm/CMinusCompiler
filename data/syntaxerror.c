int a;

int add(int x, int y) {
  return x+y;
}

void main() {
  int b
  bool x;

  x = false;
  println(true);
  println(x);

  println("This program should print the integers from 1 to n.");
  println(1);
  a = 2;
  b = 3;
  println(a);
  println(b);
  {
    int c;
    c =   a + b + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10
        - a - b - 1 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9 - 10
        + 4;
    println(c);
  }

  println(3+(1+1));
  println(3+(4-1));
  println(3+(2*2));
  println(3+(10/2));
  println(3+(16%10));

  println(add(1, add(3, 2)) + 2 + add(1, 1));
}

