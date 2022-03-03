 <div class="navigation">
  <#if currentUser??>
    <a href="/">my home</a> |
    <form id="signout" action="/signout" method="post">
      <a href="#" onclick="event.preventDefault(); signout.submit();">sign out [${currentUser.name}]</a>
      <!-- I reverted currentUser back to currentUser.name b/c it makes more sense this way !-->
    </form>
  <#else>
    <a href="/signin">sign in</a>
  </#if>
 </div>
