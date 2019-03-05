<?xml version="1.0" encoding = "UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:r="http://rainbow/db/model">
	<xsl:output method="text" indent="yes" />
	<xsl:strip-space elements="*"/>
	<xsl:template match="/">
		<xsl:apply-templates select="//r:entity" mode="create" />			
	</xsl:template>
	
	<xsl:template match="r:entity" mode="create">
		<xsl:param name="tableName" select="r:dbName"/>
		<xsl:text>create table </xsl:text>
		<xsl:value-of select="r:dbName"/>
		<xsl:text> (&#xA;</xsl:text>
		<xsl:apply-templates select="r:columns/r:column" />
		<xsl:if test="r:columns/r:column[r:key = 'true']">
			<xsl:text>,&#xA;&#x9;constraint PK_</xsl:text>
			<xsl:value-of select="r:dbName"/>
			<xsl:text> PRIMARY KEY(</xsl:text>
			<xsl:apply-templates select="r:columns/r:column[r:key = 'true']" mode="key" />
			<xsl:text>)</xsl:text>
		</xsl:if>
		<xsl:text>&#xA;) ;&#xA;&#xA;</xsl:text>
		<xsl:text>comment on table </xsl:text>
		<xsl:value-of select="r:dbName"/>
		<xsl:text> is '</xsl:text>
		<xsl:value-of select="r:cnName"/>
		<xsl:text>';&#xA;&#xA;</xsl:text>
		<xsl:for-each select="r:columns/r:column">
			<xsl:text>comment on column </xsl:text>
			<xsl:value-of select="$tableName"/>
			<xsl:text>.</xsl:text>
			<xsl:value-of select="r:dbName" />
			<xsl:text> is '</xsl:text>
			<xsl:value-of select="r:cnName"/>
			<xsl:text>';&#xA;&#xA;</xsl:text>
		</xsl:for-each>
					
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
			<xsl:text> (&#xA;</xsl:text>
			<xsl:apply-templates select="r:inxColumns/r:inxColumn" />
			<xsl:text>&#xA;);&#xA;&#xA;</xsl:text>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="r:inxColumns/r:inxColumn">
		<xsl:call-template name="inxColumn">
			<xsl:with-param name="index" select="r:name" />
			<xsl:with-param name="asc" select="r:asc" />
			<xsl:with-param name="more" select="position()!=last()" />
		</xsl:call-template>
	</xsl:template>	
	
	<xsl:template name="inxColumn">
		<xsl:param name="index"/>
		<xsl:param name="asc"/>
		<xsl:param name="more"/>
		<xsl:value-of select="$index" />
			<xsl:choose>
				<xsl:when test="$asc = 'true'">
					<xsl:text> asc </xsl:text>
				</xsl:when>
				<xsl:otherwise> desc </xsl:otherwise>
			</xsl:choose>				
		<xsl:if test="$more">,&#xA;&#xA;</xsl:if>
	</xsl:template>

	<xsl:template match="r:column" mode="key">
		<xsl:call-template name="key">
			<xsl:with-param name="column" select="r:dbName" />
			<xsl:with-param name="more" select="position()!=last()" />
		</xsl:call-template>
	</xsl:template>	
	
	<xsl:template name="key">
		<xsl:param name="column"/>
		<xsl:param name="more"/>
		<xsl:value-of select="$column" />
		<xsl:if test="$more">,</xsl:if>
	</xsl:template>

	<xsl:template match="r:column">
		<xsl:call-template name="column">
			<xsl:with-param name="column" select="r:dbName" />
			<xsl:with-param name="type" select="r:type" />
			<xsl:with-param name="mandatory" select="r:key|r:mandatory"/>
			<xsl:with-param name="more" select="position()!=last()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="column">
		<xsl:param name="column"/>
		<xsl:param name="type"/>
		<xsl:param name="mandatory"/>
		<xsl:param name="more"/>
		<xsl:text>&#x9;</xsl:text>
		<xsl:value-of select="$column" />
		<xsl:text>&#x9;&#x9;</xsl:text>
		<xsl:call-template name="type">
			<xsl:with-param name="type" select="$type" />
			<xsl:with-param name="length" select="r:length" />
			<xsl:with-param name="precision" select="r:precision" />
		</xsl:call-template>
		<xsl:if test="$mandatory = 'true'">&#x9;&#x9;not null</xsl:if>
		<xsl:if test="$more">,&#xA;</xsl:if>
	</xsl:template>
	
	<xsl:template name="type">
		<xsl:param name="type"/>
		<xsl:param name="length"/>
		<xsl:param name="precision"/>
		<xsl:choose>
			<xsl:when test="$type='CHAR'">
				<xsl:text>CHAR(</xsl:text>
				<xsl:value-of select="$length" />
				<xsl:text>) </xsl:text>
			</xsl:when>
			<xsl:when test="$type='VARCHAR'">
				<xsl:text>VARCHAR2(</xsl:text>
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
			</xsl:when>
			<xsl:when test="$type='SMALLINT'">
				<xsl:text>NUMBER(5)</xsl:text>
			</xsl:when>
			<xsl:when test="$type='INT'">
				<xsl:text>NUMBER(10)</xsl:text>
			</xsl:when>
			<xsl:when test="$type='LONG'">
				<xsl:text>NUMBER(19)</xsl:text>
			</xsl:when>
			<xsl:when test="$type='DOUBLE'">
				<xsl:text>NUMBER</xsl:text>
			</xsl:when>
			<xsl:when test="$type='NUMERIC'">
				<xsl:text>NUMBER(</xsl:text>
				<xsl:value-of select="$length" />
				<xsl:text>,</xsl:text>
				<xsl:value-of select="$precision" />
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="$type='BLOB'">
				<xsl:text>BLOB</xsl:text>
			</xsl:when>
			<xsl:when test="$type='DATE'">
				<xsl:text>DATE</xsl:text>
			</xsl:when>
			<xsl:when test="$type='TIME'">
				<xsl:text>DATE</xsl:text>
			</xsl:when>
			<xsl:when test="$type='TIMESTAMP'">
				<xsl:text>TIMESTAMP</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>