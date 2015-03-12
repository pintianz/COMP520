package miniJava.ContextualAnalyzer;

import miniJava.AbstractSyntaxTrees.Declaration;

public class MemberEntry {

  protected String id;
  protected Declaration attr;
  boolean isPrivate;
  boolean isStatic;

  MemberEntry (String id, Declaration attr, boolean isPrivate, boolean isStatic) {
    this.id = id;
    this.attr = attr;
    this.isPrivate = isPrivate;
    this.isStatic = isStatic;
  }

}

