import tester.*;

interface IPred<T> {
  boolean apply(T i);
}

class FindString implements IPred<String> {
  String target;
  
  FindString(String target) {
    this.target = target;
  }
  /* TEMPLATE:
   * 
   * METHODS:
   * 
   * apply(String in) ........ boolean
   * 
   * 
   */
  
  // Finds strings equal to "abc"
  public boolean apply(String in) {
    return in.equals(this.target);
  }
}

class Deque<T> {
  Sentinel<T> header;
  
  Deque(Sentinel<T> header) {
    this.header = header;
  }
  
  Deque() {
    this(new Sentinel<T>());
  }
  
  /* TEMPLATE
   * 
   * FIELDS:
   * 
   * header ............... Sentinel<T>
   * 
   * METHODS:
   * 
   * this.size() ........................ int
   * this.addAtHead(T obj) .............. void
   * this.addAtTail(T obj) .............. void
   * this.removeFromhead() .............. T
   * this.removeFromTail() .............. T
   * this.find(IPred<T> pred) ........... ANode<T>
   * this.removeNode(ANode<T> a) .................. void
   * 
   * FIELD METHODS: 
   * 
   * this.sentinel.size() ........................ int
   * this.sentinel.sizeHelper(ANode<T> a) .............. int
   * this.sentinel.addAtHead(T obj) .............. void
   * this.sentinel.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.sentinel.addAtTail(T obj) .............. void
   * this.sentinel.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.sentinel.removeFromhead() .............. T
   * this.sentinel.removeFromheadHelper(ANode<T> a) .... T
   * this.sentinel.removeFromTail() .............. T
   * this.sentinel.removeFromTailHelper(ANode<T> a) .. T
   * this.sentinel.find(IPred<T> pred) ........... ANode<T>
   * this.sentinel.findHelper(IPred<T> pred) ....... ANode<T>
   * this.sentinel.removeNode(ANode<T> a) ............... void
   * this.sentinel.removeNodeHelper(ANode<T> a) ......... void
   * 
   */
  
  
  // Returns size of this Deque
  int size() {
    return header.size();
  }
  
  // EFFECTS: Adds to the head the desired object
  void addAtHead(T obj) {
    header.addAtHead(obj);
  }
  
  // EFFECTS:Adds to the tail the desired object
  void addAtTail(T obj) {
    header.addAtTail(obj);
  }
  
  // Removes from the head one node and returns the item removed
  // EFFECT: Removes one node from the head
  T removeFromHead() {
    return header.removeFromHead();
  }
  
  // Removes from the tail one node and returns the item removed
  // EFFECT: Removes one node from the tail
  T removeFromTail() {
    return this.header.removeFromTail();
  }
  
  // Finds the desired node with the given pred
  ANode<T> find(IPred<T> pred) {
    return this.header.find(pred);
  }
  
  // EFFECTS: Removes a node from this deque
  void removeNode(ANode<T> a) {
    if (!a.equals(this.header)) {
      this.header.removeNode(a);
    }
  }
}

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }
  
  /* TEMPLATE
   * 
   * FIELDS:
   * 
   * next ............ ANode<T>
   * prev .............. ANode<T>
   * 
   * METHODS:
   *  
   * this.sizeHelper(ANode<T> a) .............. int
   * this.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.removeFromheadHelper(ANode<T> a) .... T
   * this.removeFromTailHelper(ANode<T> a) .. T
   * this.findHelper(IPred<T> pred) ....... ANode<T>
   * this.removeNodeHelper(ANode<T> a) ......... void
   * 
   * METHODS OF FIELDS:
   * 
   * this.next.sizeHelper(ANode<T> a) .............. int
   * this.next.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.next.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.next.removeFromheadHelper(ANode<T> a) .... T
   * this.next.removeFromTailHelper(ANode<T> a) .. T
   * this.next.findHelper(IPred<T> pred) ....... ANode<T>
   * this.next.removeNodeHelper(ANode<T> a) ......... void
   * this.prev.sizeHelper(ANode<T> a) .............. int
   * this.prev.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.prev.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.prev.removeFromheadHelper(ANode<T> a) .... T
   * this.prev.removeFromTailHelper(ANode<T> a) .. T
   * this.prev.findHelper(IPred<T> pred) ....... ANode<T>
   * this.prev.removeNodeHelper(ANode<T> a) ......... void
   */
  
  // EFFECT: Updates the next node to the given node
  void updateNext(ANode<T> next) {
    this.next = next;
  }
  
  // EFFECT: Updates the prev node to the given node
  void updatePrev(ANode<T> prev) {
    this.prev = prev;
  }
  
  // Returns size of this Deque
  int sizeHelper(ANode<T> a) {
    if (a.equals(this)) {
      return 0;
    }
    return 1 + this.next.sizeHelper(a);
  }
  
  // EFFECTS: Adds to the head the desired object
  void addAtHeadHelper(T obj, ANode<T> a) {
    new Node<T>(obj, this, a);
  }
  
  // EFFECTS:Adds to the tail the desired object
  void addAtTailHelper(T obj, ANode<T> a) {
    new Node<T>(obj, a, this);
  }
  
  // Removes from the head one node and returns the item removed
  // EFFECT: Removes one node from the head
  abstract T removeFromHeadHelper(ANode<T> a);
  
  // Removes from the tail one node and returns the item removed
  // EFFECT: Removes one node from the tail
  abstract T removeFromTailHelper(ANode<T> a);
  
  // Finds the desired node with the given pred
  abstract ANode<T> findHelper(IPred<T> pred);
  
  // EFFECTS: Removes a node from this deque
  void removeNodeHelper(ANode<T> a, ANode<T> prev) {
    if (a.equals(this)) {
      prev.updateNext(this.next);
      this.next.updatePrev(prev);
    }
    else {
      this.next.removeNodeHelper(a, this);
    }
  }
}

class Sentinel<T> extends ANode<T> {  
  Sentinel() {
    super(null, null);
    this.updatePrev(this);
    this.updateNext(this);
  }
  
  /* TEMPLATE
   * 
   * FIELDS:
   * 
   * next ...... ANode<T> 
   * prev ...... ANode<T>
   * 
   * METHODS:
   * 
   * this.sentinel.size() ........................ int
   * this.sentinel.sizeHelper(ANode<T> a) .............. int
   * this.sentinel.addAtHead(T obj) .............. void
   * this.sentinel.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.sentinel.addAtTail(T obj) .............. void
   * this.sentinel.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.sentinel.removeFromhead() .............. T
   * this.sentinel.removeFromheadHelper(ANode<T> a) .... T
   * this.sentinel.removeFromTail() .............. T
   * this.sentinel.removeFromTailHelper(ANode<T> a) .. T
   * this.sentinel.find(IPred<T> pred) ........... ANode<T>
   * this.sentinel.findHelper(IPred<T> pred) ....... ANode<T>
   * this.sentinel.removeNode(ANode<T> a) ............... void
   * this.sentinel.removeNodeHelper(ANode<T> a) ......... void
   * 
   * METHODS OF FIELDS:
   * 
   * this.next.sizeHelper(ANode<T> a) .............. int
   * this.next.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.next.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.next.removeFromheadHelper(ANode<T> a) .... T
   * this.next.removeFromTailHelper(ANode<T> a) .. T
   * this.next.findHelper(IPred<T> pred) ....... ANode<T>
   * this.next.removeNodeHelper(ANode<T> a) ......... void
   * this.prev.sizeHelper(ANode<T> a) .............. int
   * this.prev.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.prev.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.prev.removeFromheadHelper(ANode<T> a) .... T
   * this.prev.removeFromTailHelper(ANode<T> a) .. T
   * this.prev.findHelper(IPred<T> pred) ....... ANode<T>
   * this.prev.removeNodeHelper(ANode<T> a) ......... void
   * 
   */
  
  // Returns size of this Deque
  int size() {
    return this.next.sizeHelper(this);
  }
  
  // EFFECTS: Adds to the head the desired object
  void addAtHead(T obj) {
    this.next.addAtHeadHelper(obj, this);
  }
  
  // EFFECTS:Adds to the tail the desired object
  void addAtTail(T obj) {
    this.prev.addAtTailHelper(obj, this);
  }
  
  // Removes from the head one node and returns the item removed
  // EFFECT: Removes one node from the head
  T removeFromHead() {
    return this.next.removeFromHeadHelper(this);
  }
  
  // Removes from the tail one node and returns the item removed
  // EFFECT: Removes one node from the tail
  T removeFromTail() {
    return this.prev.removeFromTailHelper(this);
  }
  
  // Finds the desired node with the given pred
  public ANode<T> findHelper(IPred<T> pred) {
    return this;
  }
  
  // Finds the desired node with the given pred
  ANode<T> find(IPred<T> pred) {
    return this.next.findHelper(pred);
  }
  
  // Removes from the head one node and returns the item removed
  // EFFECT: Removes one node from the head
  public T removeFromHeadHelper(ANode<T> a) {
    throw new RuntimeException("Infinite loop kiddo");
  }
  
  // Removes from the tail one node and returns the item removed
  // EFFECT: Removes one node from the tail
  public T removeFromTailHelper(ANode<T> a) {
    throw new RuntimeException("Infinite loop kiddo");
  }
  
  // EFFECTS: Removes a node from this deque
  void removeNode(ANode<T> a) {
    this.next.removeNodeHelper(a, this);
  }
  
  // EFFECTS: Removes a node from this deque
  void removeNodeHelper(ANode<T> a, ANode<T> prev) {
    return;
  }
}

class Node<T> extends ANode<T> {
  T data;
  
  Node(T data, ANode<T> prev, ANode<T> next) {
    super(prev, next);
    this.data = data;
    
    if (prev == null || next == null) {
      throw new IllegalArgumentException("Improper node arguments");
    }
    
    this.prev.updateNext(this);
    this.next.updatePrev(this);
  }
  
  Node(T data) {
    super(null, null);
    this.data = data;
  }
 
  /* TEMPLATE
   * 
   * FIELDS:
   * 
   * next ...... ANode<T> 
   * prev ...... ANode<T>
   * data ...... T
   * 
   * METHODS:
   * 
   * this.sizeHelper(ANode<T> a) .............. int
   * this.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.removeFromheadHelper(ANode<T> a) .... T
   * this.removeFromTailHelper(ANode<T> a) .. T
   * this.findHelper(IPred<T> pred) ....... ANode<T>
   * this.removeNodeHelper(ANode<T> a) ......... void
   * 
   * METHODS OF FIELDS:
   * 
   * this.next.sizeHelper(ANode<T> a) .............. int
   * this.next.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.next.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.next.removeFromheadHelper(ANode<T> a) .... T
   * this.next.removeFromTailHelper(ANode<T> a) .. T
   * this.next.findHelper(IPred<T> pred) ....... ANode<T>
   * this.next.removeNodeHelper(ANode<T> a) ......... void
   * this.prev.sizeHelper(ANode<T> a) .............. int
   * this.prev.addAtHeadHelper(T obj, ANode<T> a) ..void
   * this.prev.addAtTailHelper(T obj, ANode<T> a) .. void
   * this.prev.removeFromheadHelper(ANode<T> a) .... T
   * this.prev.removeFromTailHelper(ANode<T> a) .. T
   * this.prev.findHelper(IPred<T> pred) ....... ANode<T>
   * this.prev.removeNodeHelper(ANode<T> a) ......... void
   * 
   */
  
  
  // Finds the desired node with the given pred
  public ANode<T> findHelper(IPred<T> pred) {
    if (pred.apply(this.data)) {
      return this;
    }
    return this.next.findHelper(pred);
  }
  
  // Removes from the head one node and returns the item removed
  T removeFromHeadHelper(ANode<T> a) {
    a.updateNext(this.next);
    this.next.updatePrev(a);
    return this.data;
  }
  
  // EFFECTS:Adds to the tail the desired object
  T removeFromTailHelper(ANode<T> a) {
    this.prev.updateNext(a);
    a.updatePrev(this.prev);
    return this.data;
  }
}

class ExamplesDeque {
  
  Deque<String> deque1;
  
  Sentinel<String> s1;
  ANode<String> a1;
  ANode<String> a2;
  ANode<String> a3;
  ANode<String> a4;
  Deque<String> deque2;
  
  Sentinel<String> s2;
  ANode<String> a12;
  ANode<String> a22;
  ANode<String> a32;
  ANode<String> a42;
  Deque<String> deque3;
  
  void init() {
    deque1 = new Deque<String>();
    
    s1 = new Sentinel<String>();
    a1 = new Node<String>("abc", s1, s1);
    a2 = new Node<String>("bcd", s1, a1);
    a3 = new Node<String>("cde", s1, a2);
    a4 = new Node<String>("def", s1, a3);
    deque2 = new Deque<String>(s1);
    
    s2 = new Sentinel<String>();
    a12 = new Node<String>("123", s2, s2);
    a22 = new Node<String>("023", s2, a12);
    a32 = new Node<String>("912", s2, a22);
    a42 = new Node<String>("561", s2, a32);
    deque3 = new Deque<String>(s2);
  }
  
  void testSize(Tester t) {
    this.init();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
  }
  
  void testAddAtHead(Tester t) {
    this.init();
    Sentinel<String> s = new Sentinel<String>();
    ANode<String> a4 = new Node<String>("abc", s, s);
    ANode<String> a5 = new Node<String>("bcd", s, a4);
    ANode<String> a6 = new Node<String>("cde", s, a5);
    ANode<String> a7 = new Node<String>("def", s, a6);
    Deque<String> dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2, dequeTemp);
    this.deque2.addAtHead("SdF");
    s = new Sentinel<String>();
    a3 = new Node<String>("SdF", s, s);
    a4 = new Node<String>("abc", s, a3);
    a5 = new Node<String>("bcd", s, a4);
    a6 = new Node<String>("cde", s, a5);
    a7 = new Node<String>("def", s, a6);
    dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2, dequeTemp);
  }
  
  void testAddAtTail(Tester t) {
    this.init();
    Sentinel<String> s = new Sentinel<String>();
    ANode<String> a4 = new Node<String>("abc", s, s);
    ANode<String> a5 = new Node<String>("bcd", s, a4);
    ANode<String> a6 = new Node<String>("cde", s, a5);
    ANode<String> a7 = new Node<String>("def", s, a6);
    Deque<String> dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2, dequeTemp);
    this.deque2.addAtTail("SdF");
    s = new Sentinel<String>();
    a4 = new Node<String>("abc", s, s);
    a5 = new Node<String>("bcd", s, a4);
    a6 = new Node<String>("cde", s, a5);
    a7 = new Node<String>("def", s, a6);
    ANode<String> a8 = new Node<String>("SdF", s, a7);
    dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2, dequeTemp);
  }
  
  void testRemoveFromHead(Tester t) {
    this.init();
    Sentinel<String> s = new Sentinel<String>();
    ANode<String> a4 = new Node<String>("abc", s, s);
    ANode<String> a5 = new Node<String>("bcd", s, a4);
    ANode<String> a6 = new Node<String>("cde", s, a5);
    ANode<String> a7 = new Node<String>("def", s, a6);
    Deque<String> dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2, dequeTemp);
    this.deque2.removeFromHead();
    s = new Sentinel<String>();
    a5 = new Node<String>("bcd", s, s);
    a6 = new Node<String>("cde", s, a5);
    a7 = new Node<String>("def", s, a6);
    dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2, dequeTemp);
  }
  
  void testRemoveFromTail(Tester t) {
    this.init();
    Sentinel<String> s = new Sentinel<String>();
    ANode<String> a4 = new Node<String>("abc", s, s);
    ANode<String> a5 = new Node<String>("bcd", s, a4);
    ANode<String> a6 = new Node<String>("cde", s, a5);
    ANode<String> a7 = new Node<String>("def", s, a6);
    Deque<String> dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2, dequeTemp);
    this.deque2.removeFromTail();
    s = new Sentinel<String>();
    a4 = new Node<String>("abc", s, s);
    a5 = new Node<String>("bcd", s, a4);
    a6 = new Node<String>("cde", s, a5);
    dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2, dequeTemp);
  }
  
  void testFind(Tester t) {
    this.init();
    Sentinel<String> s = new Sentinel<String>();
    ANode<String> a4 = new Node<String>("abc", s, s);
    ANode<String> a5 = new Node<String>("bcd", s, a4);
    ANode<String> a6 = new Node<String>("cde", s, a5);
    ANode<String> a7 = new Node<String>("def", s, a6);
    Deque<String> dequeTemp = new Deque<String>(s);
    t.checkExpect(this.deque2.find(new FindString("abc")), 
        a4);
    t.checkExpect(this.deque2.find(new FindString("dfa")), 
        s);
    t.checkExpect(this.deque1.find(new FindString("abc")), 
        new Sentinel<String>());
  }
}