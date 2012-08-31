class Test <T, U> {
  protected U exerciseOps(TestData<T> data, TerminalOp<T, U> terminal, IntermediateOp<T, U>... ops) {
    return exerciseOps(data, terminal,  (u, v) ->  u.equals(v));
  }



  class TestData<K> {}
  interface TerminalOp<L, M> extends IntermediateOp<L, M> {

  }
  interface IntermediateOp<L1, M1> { boolean _(L1 l, M1 m);}

}