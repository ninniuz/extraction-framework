package org.dbpedia.extraction.server.util

import scala.xml.Elem

/**
 * Created by andread on 07/02/14.
 */
object WebUtils {

  def getBootStrapNavBar() : Elem = {

    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Project name</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav navbar-right">
            <li><a href="#">Dashboard</a></li>
            <li><a href="#">Settings</a></li>
            <li><a href="#">Profile</a></li>
            <li><a href="#">Help</a></li>
          </ul>
          <form class="navbar-form navbar-right">
            <input type="text" class="form-control" placeholder="Search..." />
          </form>
        </div>
      </div>
    </div>
  }

  def addSidebar() : Elem = {
      <div class="col-sm-3 col-md-2 sidebar">
        <ul class="nav nav-sidebar">
          <li class="active"><a href="#">Overview</a></li>
          <li><a href="#">Reports</a></li>
          <li><a href="#">Analytics</a></li>
          <li><a href="#">Export</a></li>
        </ul>
        <ul class="nav nav-sidebar">
          <li><a href="">Nav item</a></li>
          <li><a href="">Nav item again</a></li>
          <li><a href="">One more nav</a></li>
          <li><a href="">Another nav item</a></li>
          <li><a href="">More navigation</a></li>
        </ul>
        <ul class="nav nav-sidebar">
          <li><a href="">Nav item again</a></li>
          <li><a href="">One more nav</a></li>
          <li><a href="">Another nav item</a></li>
        </ul>
      </div>
  }

  def dashboardCss() : String = {
    """
      |/*
      | * Base structure
      | */
      |
      |/* Move down content because we have a fixed navbar that is 50px tall */
      |body {
      |  padding-top: 50px;
      |}
      |
      |
      |/*
      | * Global add-ons
      | */
      |
      |.sub-header {
      |  padding-bottom: 10px;
      |  border-bottom: 1px solid #eee;
      |}
      |
      |
      |/*
      | * Sidebar
      | */
      |
      |/* Hide for mobile, show later */
      |.sidebar {
      |  display: none;
      |}
      |@media (min-width: 768px) {
      |  .sidebar {
      |    position: fixed;
      |    top: 0;
      |    left: 0;
      |    bottom: 0;
      |    z-index: 1000;
      |    display: block;
      |    padding: 70px 20px 20px;
      |    background-color: #f5f5f5;
      |    border-right: 1px solid #eee;
      |  }
      |}
      |
      |/* Sidebar navigation */
      |.nav-sidebar {
      |  margin-left: -20px;
      |  margin-right: -21px; /* 20px padding + 1px border */
      |  margin-bottom: 20px;
      |}
      |.nav-sidebar > li > a {
      |  padding-left: 20px;
      |  padding-right: 20px;
      |}
      |.nav-sidebar > .active > a {
      |  color: #fff;
      |  background-color: #428bca;
      |}
      |
      |
      |/*
      | * Main content
      | */
      |
      |.main {
      |  padding: 20px;
      |}
      |@media (min-width: 768px) {
      |  .main {
      |    padding-left: 40px;
      |    padding-right: 40px;
      |  }
      |}
      |.main .page-header {
      |  margin-top: 0;
      |}
      |
      |
      |/*
      | * Placeholder dashboard ideas
      | */
      |
      |.placeholders {
      |  margin-bottom: 30px;
      |  text-align: center;
      |}
      |.placeholders h4 {
      |  margin-bottom: 0;
      |}
      |.placeholder {
      |  margin-bottom: 20px;
      |}
      |.placeholder img {
      |  border-radius: 50%;
      |}
      |
    """.stripMargin
  }
}
