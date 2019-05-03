<?xml version="1.0" encoding = "UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:r="http://rainbow/db/model">
	<xsl:output method="text" indent="yes" encoding="UTF-8" />
	<xsl:strip-space elements="*"/>
	<xsl:template match="/">
		<xsl:apply-templates select="//r:entity" mode="drop" />
		<xsl:apply-templates select="//r:entity" mode="create" />
	</xsl:template>

	<xsl:template match="r:entity" mode="drop">
		<xsl:for-each select="r:indexes/r:index">
			<xsl:text>drop index if exists </xsl:text>
			<xsl:value-of select="r:name" />
			<xsl:text>;&#xA;&#xA;</xsl:text>
		</xsl:for-each>
		<xsl:text>DROP TABLE IF EXISTS </xsl:text>
		<xsl:value-of select="r:dbName"/>
		<xsl:text> CASCADE;&#xA;&#xA;</xsl:text>
	</xsl:template>

	<xsl:template match="r:entity" mode="create">
		<xsl:param name="tableName" select="r:dbName"/>
		<xsl:text>CREATE TABLE </xsl:text>
		<xsl:value-of select="r:dbName"/>
		<xsl:text> (&#xA;</xsl:text>
		<xsl:apply-templates select="r:columns/r:column" />
		<xsl:if test="r:columns/r:column[r:key = 'true']">
			<xsl:text>,&#xA;&#x9;CONSTRAINT PK_</xsl:text>
			<xsl:value-of select="r:dbName"/>
			<xsl:text> PRIMARY KEY(</xsl:text>
			<xsl:apply-templates select="r:columns/r:column[r:key = 'true']" mode="key" />
			<xsl:text>) </xsl:text>
		</xsl:if>
		<xsl:text>&#xA;);&#xA;&#xA;</xsl:text>
		<xsl:for-each select="r:indexes/r:index">
			<xsl:choose>
				<xsl:when test="r:unique = 'true'">
					<xsl:text>create unique index </xsl:text>
				</xsl:when>
				<xsl:otherwise>create index </xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="r:name" />
			<xsl:text> on </xsl:text>
			<xsl:value-of select="$tableName"/>
			<xsl:text>(</xsl:text>
			<xsl:apply-templates select="r:inxColumns/r:inxColumn" />
			<xsl:text>);&#xA;&#xA;</xsl:text>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="r:inxColumns/r:inxColumn">
		<xsl:value-of select="r:name" />
		<xsl:if test="r:asc = 'false'"> desc</xsl:if>
		<xsl:if test="position()!=last()">,</xsl:if>
	</xsl:template>

	<xsl:template match="r:column">
		<xsl:text>&#x9;</xsl:text>
		<xsl:value-of select="r:dbName" />
		<xsl:text>&#x9;</xsl:text>
		<xsl:call-template name="type">
			<xsl:with-param name="type" select="r:type" />
			<xsl:with-param name="length" select="r:length" />
			<xsl:with-param name="precision" select="r:precision" />
		</xsl:call-template>
		<xsl:if test="r:key|r:mandatory='true'">&#x9;not null</xsl:if>
		<xsl:if test="position()!=last()">,&#xA;</xsl:if>
	</xsl:template>

	<xsl:template name="column">
		<xsl:param name="type"/>
		<xsl:param name="mandatory"/>
		<xsl:param name="more"/>
		<xsl:text>&#x9;&#x9;</xsl:text>
		<xsl:call-template name="type">
			<xsl:with-param name="type" select="$type" />
			<xsl:with-param name="length" select="r:length" />
			<xsl:with-param name="precision" select="r:precision" />
		</xsl:call-template>
		<xsl:if test="$mandatory = 'true'">&#x9;&#x9;not null</xsl:if>
		<xsl:if test="$more"></xsl:if>
	</xsl:template>

	<xsl:template name="type">
		<xsl:param name="type"/>
		<xsl:param name="length"/>
		<xsl:param name="precision"/>
		<xsl:choose>
			<xsl:when test="$type='CHAR'">
				<xsl:text>CHAR(</xsl:text>
				<xsl:value-of select="$length" />
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="$type='VARCHAR'">
				<xsl:text>VARCHAR(</xsl:text>
				<xsl:choose>
				<xsl:when test="$length=0">4000</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$length" />
				</xsl:otherwise>
				</xsl:choose>
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="$type='CLOB'">
				<xsl:text>CLOB</xsl:text>
				<xsl:if test="$length>0">
					<xsl:text>(</xsl:text>
					<xsl:value-of select="$length" />
					<xsl:text>)</xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$type='SMALLINT'">
				<xsl:text>SMALLINT</xsl:text>
			</xsl:when>
			<xsl:when test="$type='INT'">
				<xsl:text>INT</xsl:text>
			</xsl:when>
			<xsl:when test="$type='LONG'">
				<xsl:text>BIGINT</xsl:text>
			</xsl:when>
			<xsl:when test="$type='DOUBLE'">
				<xsl:text>DOUBLE</xsl:text>
			</xsl:when>
			<xsl:when test="$type='NUMERIC'">
				<xsl:text>DECIMAL(</xsl:text>
				<xsl:value-of select="$length" />
				<xsl:text>,</xsl:text>
				<xsl:value-of select="$precision" />
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="$type='BLOB'">
				<xsl:text>BLOB</xsl:text>
				<xsl:if test="$length>0">
					<xsl:text>(</xsl:text>
					<xsl:value-of select="$length" />
					<xsl:text>)</xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$type='DATE'">
				<xsl:text>DATE</xsl:text>
			</xsl:when>
			<xsl:when test="$type='TIME'">
				<xsl:text>TIME</xsl:text>
			</xsl:when>
			<xsl:when test="$type='TIMESTAMP'">
				<xsl:text>TIMESTAMP</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="r:column" mode="key">
		<xsl:value-of select="r:dbName" />
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>