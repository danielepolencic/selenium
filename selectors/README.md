# Selectors

- Reverse from a perspective of how do I use them in my context
- how to find ids, classes, and test
- example with bootstrap?
- chaining classes .one.and.two
- group :nth-child() and first, last. say it's about selecting elements

## CSS

You are probably already familiar with CSS selectors such as `id`, `class` and
`element`. If you rely on those only when you write your element locator you may
have missed out on the great flexibility that comes with the rest of the css
grammar.

### Element Selector

Selects elements in the current page with the same type.

```html
<body>
  <article>First</article>
  <div>Second</div>
</body>
```

```java
driver.findElement(By.css('article')).getText(); // First
driver.findElement(By.css('div')).getText(); // Second
```

When there's more than one element with the same type, only the first is
returned when you use `findElement` or all of them are returned as a list when
you use `findElements`.


```html
<body>
  <ul>
    <li>Red</li>
    <li>Blue</li>
  </ul>
</body>
```

```java
driver.findElement(By.css('li')).getText(); // Red
driver.findElements(By.css('li')).size(); // 2
```

### Class Selector

Selects all the elements with a given class. When you search for a class in the
current page, the name of the class is prefixed by a `.` dot. Hence if you're
looking for an element with a `description` class, you selector is `.description`.

```html
<body>
  <div class="description">Test</div>
  <div class="date">Today</div>
</body>
```

```java
driver.findElement(By.css('.description')).getText(); // Test
```

The class selector matches all the elements with the same class. It doesn't care
about the element type, or how many classes are attached to that element.

```html
<div class="title">
  <div class="item">One</div>
  <span class="item strong">Two</span>
</div>
```

```java
driver.findElements(By.css('.item')).size(); // 2
```

### ID Selector

Selects the element with the same id. The id selector is very similar to the
class selector except that:

  - its prefix is an `#` hash
  - there can only by one id in the entire page

```html
<body>
  <div id="logo">Stark</div>
</body>
```

```java
driver.findElements(By.css('#logo')).getText(); // Stark
```

If there can only be one id per page, what happens when you search for multiple
elements with the same ids?

```html
<div class="title">
  <div id="item">One</div>
  <span id="item strong">Two</span>
</div>
```

```java
driver.findElements(By.css('#item')).size(); // ??
```

[TODO]

### Combining selectors - Descendant Selector (li a)

Being able to select elements on the page using elements, classes or ids only is
very convenient, but may still not be specific enough. Classes are reused
frequently in more than a single widget and searching for a specific element can
be challenging.

But you can be more specific by using a combination of ids, classes and
elements. As long as you list your ids, classes and/or elements and you match
the hirerachy of the page, you can target a very specific element.

```
<body>
  <div>
    <span class="item">First item</span>
  </div>
  <ul>
    <li class="item">Second item</li>
  </ul>
  <section id="footer">
    <ul>
      <li class="item">Third item</li>
    </ul>
  </section>
</body>
```

```java
driver.findElement(By.css('div .item')).getText(); // First Item
```

Selectors don't have to match the hierarchy of the page exactly. As long as the
first selector contains the following selector, you'll be able to
locate the element in the page.

```java
driver.findElement(By.css('#footer ul .item')).getText(); // Third Item
driver.findElement(By.css('#footer .item')).getText(); // Third Item
```

### Child Selector >

Being able to not specify the hierarchy of the page when you use a combination
of different selectors is a blessing and a curse. Like in this case:

```html
<body>
  <section>
    <div>Inner div</div>
  </section>
  <div>Outer div</div>
</body>
```

Let's select the element with `Outer div` as text. You may be tempted to do:

```java
driver.findElement(By.css('body div')).getText(); // Inner div
```

The idea to use `body div` selector is good. But that selector selects all divs
within body and the first occurence is `Inner div`. What if you could target
just childrens of an element? You would be able to target the div that is just
undernith body. You can select only the immediate children of an element using
the `>` sign.

```java
driver.findElement(By.css('body > div')).getText(); // Outer div
```

### Adjacent Sibling Selector +

The child selector is very useful when the hierarchy is very deep. But when the
html structure is flat - such as in this example - you may find useful the
sibling selector.

```html
<body>
  <div>One</div>
  <span>Two</span>
  <div>Three</div>
</body>
```

With the sibling selector, you can select an element which is next to another
element. Let's select the div with text `Three`:

```java
driver.findElement(By.css('body span + div')).getText(); // Three
```

The sibling selector `+` plus reads as follow: from within the body, select the
div element next to the span element.

### General Sibling Selector ~ (selects only if there's another element before)

And what if you want to select a sibling element that is not position next to
the element you're interested in? In that case you can use the general sibling
selector `~` tilde.

The general sibling selector selects an element only if the other selector
matches in the current level.

In this snippet, you are asked to select all the subtitles for an article that
has both subheading and subtitle.

```html
<body>
  <section>
    <div>Title 1</div>
    <span>Subtitle 1</span>
  </section>
  <section>
    <p>Subheading 2</p>
    <div>Title 2</div>
    <span>Subtitle 2</span>
  </section>
</body>
```

```java
driver.findElement(By.css('section p ~ span')).getText(); // Subtitle 2
```

### Attribute Selector

Attribute selectors let you target an element based on its attributes. In
practise this means you could target an element based on its `href` being
`http://google.come` or if the `type` equals `email`.

The simplest selector is the attribute selector. It doesn't do any comparison,
it only targets element with a specific attribute.

If you want all the `href` on the page, you can say:

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElements(By.css('[href]')).size(); // 2
```

#### Attribute equals

Let's select all the `href`s pointing to Google:

When you want a specific attribute with a certain value, you enclose in brackets
the attribute you want to target and the value it should contain.

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElement(By.css('[href="http://google.com"]')).getText(); // Google
```

#### Attribute Begins With

You can select all the links starting with `https` with the `^=` begins with
operator:

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElements(By.css('[href^="https"]')).getText(); // Facebook
```

#### Attribute Ends With

Similarly, you can select all links ending with `co.uk` using the `$=` attribute
ends operator:

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElements(By.css('[href$="co.uk"]')).getText(); // Google
```

#### Attributes Contains

We've covered matching a value of an attribute by the beginning ot the end
value. You can also match the content of the attribute using `*=` attribute
contains.

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElement(By.css('[href*="google"]')).getText(); // Google
```

#### Attribute Contains Word

When the attribute contains several values, such as in the case of a `class`
attribute - you can target the attribute only if it contains a given word.

The `~=` attribute contains word selector is very similar to Attribute contains you
learn about earlier. The difference is that whereas the attribute contains
peform a search for a substring, attribute contains word looks for a word - i.e.
a word with a space on its left and right position.

```html
<body>
  <div class="bigone">Title</div>
  <div class="one two three">Digits</div>
  <div class="a b c">Letters</div>
</body>
```

```java
driver.findElement(By.css('[class*="one"]')).getText(); // Title
driver.findElement(By.css('[class~="one"]')).getText(); // Digits
```

### Pseudo Classes

#### a:link, a:visted
`:link` and `:visited` pseudo-classes are useful to select links on the page
that have or have not been visited already respectively.

When you visit a page for the first time and you're presented with this page

```html
<body>
  <a href="http://domain.com/1">One</a>
  <a href="http://domain.com/2">Two</a>
</body>
```

all the links are new - they haven't been visited yet.

```java
driver.findElements(By.css('a:link')).size(); // 2
driver.findElements(By.css('a:visited')).size(); // 0
```

However, as soon as you click on the first link:

```java
driver.findElements(By.css('a:link')).size(); // 1
driver.findElements(By.css('a:visited')).size(); // 1
```

#### :first-child, :last-child

As the name suggest, when you have a list of elements that share a common trait -
such as a class, an attribute or an element - you can select the first or last
child in that list.

```html
<body>
  <ul>
    <li>One</li>
    <li>Two</li>
    <li>Three</li>
  </ul>
</body>
```

```java
driver.findElement(By.css('li:first')).getText(); // One
driver.findElement(By.css('li:last')).getText(); // Three
```

#### :nth-child()

If first and last elements are not what you're looking for, you can select the
nth element in the list with the `:nth-child()` pseudo-class:

```html
<body>
  <ul>
    <li>One</li>
    <li>Two</li>
    <li>Three</li>
  </ul>
</body>
```

```java
driver.findElement(By.css('li:nth-child(2)')).getText(); // Two
```


:first-of-type, :last-of-type
:nth-of-type()
:nth-last-child()
:nth-last-of-type()
:only-child
:only-of-type
:empty
:not()
:checked
:enabled, :disabled

### Universal Selector

## xpath
### axis specifiers
### tests
### predicates
### functions and operators

## xpath vs css
