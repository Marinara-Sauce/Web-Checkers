<span class="rating">
<#if player??>
    (${player.rating})
<#else>
    (${currentUser.rating})
</#if>
</span>